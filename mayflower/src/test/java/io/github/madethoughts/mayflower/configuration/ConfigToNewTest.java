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

import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.madethoughts.mayflower.internal.Preconditions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plugin.TestPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.gitlab.taucher2003.t2003_utils.common.StringUtils.normalizeNewlines;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConfigToNewTest extends MayflowerConfigurationTest {

    @Override
    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        createConfig();
    }

    @Override
    protected Path templateConfig() {
        return RESOURCES_ROOT.resolve("to-new.yml");
    }

    @Test
    void doesNotChangeConfigFile() throws IOException {
        try {
            plugin = MockBukkit.load(TestPlugin.class);
        } catch (Preconditions.PreconditionException expected) {
        }

        assertThat(normalizeNewlines(Files.readString(config)))
                .isEqualTo(normalizeNewlines(Files.readString(templateConfig())));
    }

    @Test
    void throwsException() {
        assertThatThrownBy(() -> plugin = MockBukkit.load(TestPlugin.class))
                .hasMessage("Current config is newer than latest config class version.")
                .isInstanceOf(Preconditions.PreconditionException.class);
    }
}
