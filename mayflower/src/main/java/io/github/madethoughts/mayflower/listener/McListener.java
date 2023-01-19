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

import io.micronaut.aop.Adapter;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 This annotation works mostly like {@link EventListener}, except that it generates an {@link McEventListener}
 implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Adapter(McEventListener.class)
@Indexed(McEventListener.class)
@Singleton
public @interface McListener {

    /**
     @return The events priority
     @see EventHandler#priority()
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     @return whether this listener should ignore cancelled events
     @see EventHandler#ignoreCancelled()
     */
    boolean ignoreCancelled() default false;
}
