/*
 * Copyright 2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.madethoughts.mayflower.configuration;

import io.github.madethoughts.mayflower.configuration.internal.Configs;
import io.github.madethoughts.mayflower.internal.Preconditions;
import io.github.madethoughts.mayflower.lifecycle.event.internal.PreLoadEvent;
import io.github.madethoughts.mayflower.plugin.MayflowerPlugin;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 This listener is responsibly for setting up and migrating the plugin's config.
 */
@Singleton
public class DefaultConfigInitializer implements ApplicationEventListener<PreLoadEvent> {

    private static final Logger log = LoggerFactory.getLogger(DefaultConfigInitializer.class);
    private static final String CONFIG_YML = "config.yml";
    private static final String DEFAULT_CONFIG_YML = "default-config.yml";
    private final Path configPath;
    private final ResourceLoader resourceLoader;
    private final ApplicationContext context;
    private final FileConfiguration configuration;

    @SuppressWarnings("MissingJavadoc")
    public DefaultConfigInitializer(MayflowerPlugin mayflowerPlugin, ResourceLoader resourceLoader) {
        configPath = mayflowerPlugin.getDataFolder().toPath().resolve(CONFIG_YML);
        this.resourceLoader = resourceLoader;
        this.context = mayflowerPlugin.applicationContext();
        configuration = mayflowerPlugin.getConfig();
    }

    private static double readVersion(FileConfiguration config) {
        var version = config.get("version");
        Preconditions.checkCondition(() -> version instanceof Double,
                "'version' field of config must be type double, eg. `version: 3.6`; not `version: '3.6'` or something"
        );
        return (double) version;
    }

    @Override
    public void onApplicationEvent(PreLoadEvent event) {
        var defaultConfigOpt = resourceLoader.getResourceAsStream(DEFAULT_CONFIG_YML);

        if (defaultConfigOpt.isEmpty()) {
            log.info("No default config found in jar, skip config setup");
            return;
        }

        try (var defaultConfigStream = defaultConfigOpt.orElseThrow()) {
            var defaultConfig = new String(defaultConfigStream.readAllBytes(), StandardCharsets.UTF_8);
            if (Files.exists(configPath)) {
                migrate(defaultConfig);
            } else {
                Files.createDirectories(configPath.getParent());
                Files.writeString(configPath, defaultConfig);
            }
        } catch (IOException | InvalidConfigurationException e) {
            log.error("Error copying default config or migrating config", e);
        }
    }

    private void migrate(String defaultConfigString) throws InvalidConfigurationException, IOException {
        var defaultConfig = new YamlConfiguration();
        defaultConfig.loadFromString(defaultConfigString);

        var currentVersion = readVersion(configuration);
        var latestVersion = readVersion(defaultConfig);

        validateDefaultConfigVersion(latestVersion);

        Preconditions.checkCondition(() -> currentVersion <= latestVersion,
                "Current config is newer than latest config class version."
        );

        // no migrations needed
        //noinspection FloatingPointEquality
        if (currentVersion == latestVersion) return;
        log.info("Starting migration from {} to {}", currentVersion, latestVersion);

        backupConfig(currentVersion);

        // set default config as new default config
        configuration.addDefaults(defaultConfig);

        // check if versions are different major versions. If they are, run config migrations.
        if (Math.floor(latestVersion) != Math.floor(currentVersion)) {
            log.info("Starting major version migration, this will add new, remove, rename or modify existing options.");
            runConfigMigrations(currentVersion, latestVersion);
        } else {
            log.info("No breaking config version detected, only add new config options... ");
        }

        // cleanup config -- removing all keys that are not in default config
        cleanup(defaultConfig);

        configuration.options().copyDefaults(true);
        configuration.set("version", latestVersion);
        configuration.save(configPath.toFile());
        log.info("Successfully migrated config from {} to {}", currentVersion, latestVersion);
    }

    private void backupConfig(double currentVersion) throws IOException {
        var backupDir = configPath.getParent().resolve("config_backups");
        var date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
        var backupName = "%s+%s_config.yml".formatted(currentVersion, date);
        var backupPath = backupDir.resolve(backupName);
        log.info("Creating backup '{}' of config with version {}", backupName, currentVersion);

        Files.createDirectories(backupDir);
        if (Files.exists(backupPath)) {
            log.info("Today's config backup already exists, overriding...");
        }
        configuration.save(backupPath.toFile());
    }

    private void validateDefaultConfigVersion(double defaultConfigVersion) {
        context.getBeanDefinitions(Object.class, Qualifiers.byStereotype(PluginConfig.class))
               .stream()
               .filter(Configs::isRoot)
               .map(Configs::version)
               .findAny()
               .ifPresent(pluginConfigVersion -> Preconditions.checkCondition(
                       () -> pluginConfigVersion == defaultConfigVersion,
                       "Default config version is unequal latest version from plugin config classes."
               ));
    }

    private void cleanup(FileConfiguration defaultConfig) {
        log.info("Starting config cleanup, all unnecessary options will be removed.");
        for (var key : configuration.getKeys(true)) {
            if (!defaultConfig.contains(key)) {
                log.info("Removed option: {}", key);
                configuration.set(key, null);
            }
        }
    }

    private void runConfigMigrations(double currentVersion, double latestVersion) {
        var migrations =
                context.getBeanDefinitions(ConfigMigration.class, Qualifiers.byStereotype(Migration.class))
                       .stream()
                       .map(def -> new Pair(def.intValue(Migration.class).orElseThrow(), def))
                       .filter(pair -> pair.version() > currentVersion)
                       .sorted(Comparator.comparingDouble(Pair::version))
                       .toList();

        Preconditions.checkCondition(
                () -> (int) (Math.floor(latestVersion) - Math.floor(currentVersion)) == migrations.size(),
                "Missing major config version migrations found."
        );
        for (var migration : migrations) {
            var version = migration.version();
            var definition = migration.definition();
            log.info("Running config migration: {} -> {} : {}", version - 1, version, definition.getName());
            context.getBean(definition.getBeanType()).migrate(configuration);
        }
    }

    private record Pair(double version, BeanDefinition<? extends ConfigMigration> definition) {}
}
