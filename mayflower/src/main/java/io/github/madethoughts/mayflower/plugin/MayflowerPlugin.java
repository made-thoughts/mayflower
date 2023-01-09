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

package io.github.madethoughts.mayflower.plugin;

import io.github.madethoughts.mayflower.lifecycle.event.DisableEvent;
import io.github.madethoughts.mayflower.lifecycle.event.EnableEvent;
import io.github.madethoughts.mayflower.lifecycle.event.LoadEvent;
import io.micronaut.context.ApplicationContext;
import org.bukkit.plugin.java.JavaPlugin;

/**
 This class is the main entry point for each mayflower plugin. It replaces paper's {@link JavaPlugin} class.
 The class is responsibly for firing the plugin's lifecycle events and setting up the {@link ApplicationContext}.
 <p>
 The methods {@link JavaPlugin#onLoad()}, {@link JavaPlugin#onEnable()} and {@link JavaPlugin#onDisable()} cannot be
 overridden,the corresponding events should be used.
 <p>
 The inheritor of this class should not handle any business logic.
 */
public class MayflowerPlugin extends JavaPlugin {

    private final ApplicationContext applicationContext;

    protected MayflowerPlugin() {
        applicationContext = ApplicationContext.run(getClassLoader()).start();
    }

    @Override
    public final void onLoad() {
        applicationContext.registerSingleton(this);
        applicationContext.registerSingleton(getServer());

        applicationContext.getEventPublisher(LoadEvent.class).publishEvent(new LoadEvent());
    }

    @Override
    public final void onDisable() {
        applicationContext.getEventPublisher(DisableEvent.class).publishEvent(new DisableEvent());
        applicationContext.close();
    }

    @Override
    public final void onEnable() {
        applicationContext.getEventPublisher(EnableEvent.class).publishEvent(new EnableEvent());
    }

    /**
     @return The used {@link ApplicationContext}
     */
    public final ApplicationContext applicationContext() {
        return applicationContext;
    }
}
