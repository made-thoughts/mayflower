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

package io.github.madethoughts.mayflower.testplugin;

import io.github.madethoughts.mayflower.listener.McEventListener;
import io.github.madethoughts.mayflower.listener.McListener;
import org.bukkit.event.player.PlayerJoinEvent;

@McListener
public class PlayerJoinListener
        implements McEventListener<PlayerJoinEvent> {
    @Override
    public void onEvent(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("manuell mc event listener");
    }

    @Override
    public boolean isSupported(PlayerJoinEvent event) {
        return false;
    }
}
