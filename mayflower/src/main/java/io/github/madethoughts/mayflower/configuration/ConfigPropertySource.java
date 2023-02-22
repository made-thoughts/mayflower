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

import io.github.madethoughts.mayflower.plugin.MayflowerPlugin;
import io.micronaut.context.env.PropertySource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 Loads values from the plugin config.
 */
public class ConfigPropertySource implements PropertySource {
    private final MayflowerPlugin mayflowerPlugin;

    /**
     @param mayflowerPlugin The {@link MayflowerPlugin} instance
     */
    public ConfigPropertySource(MayflowerPlugin mayflowerPlugin) {
        this.mayflowerPlugin = mayflowerPlugin;
    }

    @Override
    public String getName() {
        return "Plugin config";
    }

    @Override
    public @Nullable Object get(String key) {
        if (!key.startsWith(PluginConfig.PLUGIN_PREFIX)) return null;
        // normally we have to subtract one from the position, but we need to remove the . after the plugin prefix,
        // so we're actually adding one position if we don't subtract one
        return mayflowerPlugin.getConfig().get(key.substring(PluginConfig.PLUGIN_PREFIX.length()));
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return mayflowerPlugin.getConfig().getKeys(true)
                .stream()
                .map("plugin.%s"::formatted)
                .iterator();
    }
}
