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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class MinorMigrationTest extends MayflowerConfigurationTest {

    @Test
    void addsMissingProperties() throws IOException {
        var oldConfig = Files.readAllLines(templateConfig());
        var newConfig = Files.readAllLines(config);

        assertThat(newConfig).hasSize(oldConfig.size() + 1);
        // validates also that the migration has not been called, as that would result in another value
        assertThat(newConfig).contains("valueToMigrate: nope");
    }

    @Override
    protected Path templateConfig() {
        return RESOURCES_ROOT.resolve("minor-migration.yml");
    }
}
