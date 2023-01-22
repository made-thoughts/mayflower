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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 Implementations of this interface are config migrations, which will update the config from one to the next specific
 version. After all migrations, the config is written back to the file.
 @see Migration
 */
@FunctionalInterface
public interface ConfigMigration {
    /**
     @param config The current configuration, returned from {@link JavaPlugin#getConfig()} and modified by previous
     migrations. The default version of this {@link FileConfiguration} is the jar's one. That one generated at
     compile time from your {@link PluginConfig} classes.
     */
    void migrate(FileConfiguration config);
}
