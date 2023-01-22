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

import io.github.madethoughts.mayflower.plugin.MayflowerTest;
import org.junit.jupiter.api.Test;
import plugin.PlayerJoinEventListener;

import static org.assertj.core.api.Assertions.assertThat;

public class EventListenerTest extends MayflowerTest {

    @Test
    void callsEventListeners() {
        server.addPlayer();

        assertThat(plugin.applicationContext().getBean(PlayerJoinEventListener.class).getEventCalls()).isEqualTo(1);
    }
}
