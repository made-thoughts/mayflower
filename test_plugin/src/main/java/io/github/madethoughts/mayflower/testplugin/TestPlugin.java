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

import io.github.madethoughts.mayflower.configuration.Default;
import io.github.madethoughts.mayflower.configuration.PluginConfig;
import io.github.madethoughts.mayflower.lifecycle.event.EnableEvent;
import io.github.madethoughts.mayflower.lifecycle.event.internal.PreLoadEvent;
import io.github.madethoughts.mayflower.listener.McListener;
import io.github.madethoughts.mayflower.plugin.MayflowerPlugin;
import io.github.madethoughts.mayflower.plugin.McPlugin;
import io.micronaut.runtime.event.annotation.EventListener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ALL")
@McPlugin(name = "MayflowerTestPlugin", version = "0.1", apiVersion = McPlugin.ApiVersion.V1_19, authors = "goldmensch")
public class TestPlugin extends MayflowerPlugin {

    private static final Logger log = LoggerFactory.getLogger(TestPlugin.class);

    @EventListener
    public void sendWorksMessage(EnableEvent event) {
        getLogger().info("Works, wuhu!");
    }

    @EventListener
    public void sendProperties(EnableEvent event) {
        var property = applicationContext().containsProperty("test.val");
        getSLF4JLogger().info("Property: {}", property);
        getSLF4JLogger().info("Default loading: {}", applicationContext().getEnvironment().getPropertySourceLoaders());

        var configFoo = getConfig().get("foo");
        log.info("Config: {}", configFoo);

        var foo = applicationContext().getBean(TestConfig.class).migrate2();
        log.info("migrate2: {}", foo);

        var propFoo = applicationContext().getProperty("plugin.foo", String.class);
        log.info("prop: {}", propFoo);
    }

    @EventListener
    public void testPreLoad(PreLoadEvent event) {
        var foo = applicationContext().getBean(TestConfig.class).migrate2();
        log.info("migrate2 (pre laod): {}", foo);
    }

    @McListener
    public void test(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("test");
    }

    @PluginConfig(version = 4.4)
    public interface InnerConfig {
        @Default("innerValue lol")
        String innerClassValue();
    }

}
