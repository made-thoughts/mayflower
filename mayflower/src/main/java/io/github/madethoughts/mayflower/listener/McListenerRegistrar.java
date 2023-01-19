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

package io.github.madethoughts.mayflower.listener;

import io.github.madethoughts.mayflower.lifecycle.event.EnableEvent;
import io.github.madethoughts.mayflower.plugin.MayflowerPlugin;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class registers all mc listeners implemented using {@link McEventListener} and {@link McListener} on plugin enable
 */
@Singleton
public final class McListenerRegistrar implements ApplicationEventListener<EnableEvent> {

    private static final Logger log = LoggerFactory.getLogger(McListenerRegistrar.class);

    private final MayflowerPlugin plugin;

    @SuppressWarnings("MissingJavadoc")
    public McListenerRegistrar(MayflowerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onApplicationEvent(EnableEvent event) {
        var applicationContext = plugin.applicationContext();
        var pluginManager = plugin.getServer().getPluginManager();
        for (var listener : applicationContext.getBeansOfType(McEventListener.class)) {
            var definition = applicationContext.getBeanDefinition(listener.getClass());
            var listenerAnnotation = definition.getAnnotation(McListener.class);

            if (listenerAnnotation == null) {
                log.error("Implementation {} of McEventListener interface have to be annotated with @McListener",
                        listener.getClass()
                );
                continue;
            }

            @SuppressWarnings("unchecked") // type safety enforced by generics in McEventListener
            var eventClass =
                    (Class<? extends Event>) definition.getTypeArguments(McEventListener.class).get(0).getType();
            var priority = listenerAnnotation.getRequiredValue("priority", EventPriority.class);
            var ignoreCancelled = listenerAnnotation.getRequiredValue("ignoreCancelled", boolean.class);
            @SuppressWarnings({"rawtypes", "unchecked"}) // type safety enforced by generics in McEventListener
            var executor = new McEventListenerExecutor(listener, eventClass);

            pluginManager.registerEvent(eventClass, listener, priority, executor, plugin, ignoreCancelled);
            log.debug("Listener {} registered on mc event {} with priority {} and ignoreCancelled {}",
                    listener.getClass(), eventClass, priority, ignoreCancelled
            );
        }
    }

    private static final class McEventListenerExecutor<T extends Event> implements EventExecutor {
        private final McEventListener<? super T> actualListener;
        private final Class<? extends T> eventClass;

        private McEventListenerExecutor(McEventListener<? super T> actualListener, Class<? extends T> eventClass) {
            this.actualListener = actualListener;
            this.eventClass = eventClass;
        }

        @Override
        public void execute(@NotNull Listener listener, @NotNull Event event) {
            var castedEvent = eventClass.cast(event);
            if (actualListener.isSupported(castedEvent)) actualListener.onEvent(castedEvent);
        }
    }
}
