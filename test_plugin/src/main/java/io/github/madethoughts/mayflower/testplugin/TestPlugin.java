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

import io.github.madethoughts.mayflower.lifecycle.event.EnableEvent;
import io.github.madethoughts.mayflower.listener.McListener;
import io.github.madethoughts.mayflower.plugin.MayflowerPlugin;
import io.github.madethoughts.mayflower.plugin.McPlugin;
import io.micronaut.runtime.event.annotation.EventListener;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("ALL")
@McPlugin(name = "MayflowerTestPlugin", version = "0.1", apiVersion = McPlugin.ApiVersion.V1_19, authors = "goldmensch")
public class TestPlugin extends MayflowerPlugin {

    @EventListener
    public void sendWorksMessage(EnableEvent event) {
        getLogger().info("Works, wuhu!");
    }

    @McListener
    public void test(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("test");
    }

}
