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

package io.github.madethoughts.mayflower.configuration;

import org.junit.jupiter.api.Test;
import plugin.Config;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigAfterMigrationTest extends MayflowerConfigurationTest {

    @Test
    void keepsCustomValues() {
        var config = plugin.applicationContext().getBean(Config.class);
        assertThat(config.someValue()).isEqualTo("Not default value");
    }

    @Test
    void loadsCorrectlyAfterMigration() {
        var config = plugin.applicationContext().getBean(Config.class);
        assertThat(config.valueToMigrate()).isEqualTo("migrated");
    }

    @Override
    protected Path templateConfig() {
        return RESOURCES_ROOT.resolve("unmigrated-with-value.yml");
    }
}
