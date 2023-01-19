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

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.EventListener;

/**
 The implementation of this interface is called, if the specific mc event is fired.

 @param <T> The specific {@link Event} for which this listener should be registered. */
@FunctionalInterface
public interface McEventListener<T extends Event> extends EventListener, Listener {
    /**
     This method is called, when the mc event is fired.

     @param event The passed bukkit event
     */
    void onEvent(T event);

    /**
     @param event The event instance
     @return if {@link this#onEvent(Event)} should be called, default true
     */
    default boolean isSupported(T event) {
        return true;
    }

}
