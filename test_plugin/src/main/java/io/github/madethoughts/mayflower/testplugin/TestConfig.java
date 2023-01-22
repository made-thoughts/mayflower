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

@SuppressWarnings("ALL")
@PluginConfig(version = 4.4)
public interface TestConfig {
    @Default("test lol")
    String foo();

    @Default("new 1.4 option")
    String newOption();

    @Default("yeeeees")
    String migrate2();
    @Default("yeeeees")
    String migrate3();
    @Default("yeeeees")
    String migrate4();

    TestSection testSection();

    @PluginConfig("test")
    public interface TestSection {
        @Default("12")
        int bar();

        @Default("asdfasdf")
        String someOther();

        InnerTest innertest();

        @PluginConfig("innerTest")
        public interface InnerTest {
            @Default("barInner")
            String barInnerVar();
        }
    }
}
