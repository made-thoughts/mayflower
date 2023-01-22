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

import org.junit.jupiter.api.Test;
import plugin.LifecycleEventListener;

import static org.assertj.core.api.Assertions.assertThat;

public class MayflowerPluginTest extends MayflowerTest {

    @Test
    void onEnable() {
        assertThat(plugin.applicationContext().getBean(LifecycleEventListener.class).getOnEnableCalls()).isEqualTo(1);
    }

    @Test
    void onLoad() {
        assertThat(plugin.applicationContext().getBean(LifecycleEventListener.class).getOnLoadCalls()).isEqualTo(1);
    }

    @Test
    void onDisable() {
        var listener = plugin.applicationContext().getBean(LifecycleEventListener.class);
        server.getPluginManager().disablePlugin(plugin);
        assertThat(listener.getOnDisableCalls()).isEqualTo(1);
    }
}
