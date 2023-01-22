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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.gitlab.taucher2003.t2003_utils.common.StringUtils.normalizeNewlines;
import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTest extends MayflowerConfigurationTest {

    @Test
    void isMigrated() throws IOException {
        assertThat(Files.readAllLines(config)).contains("someValue: some-default-value");
    }

    @Test
    void savesBackup() throws IOException {
        var backupFileName = "1.0+%s_config.yml".formatted(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()));
        var backupFilePath = config.getParent().resolve("config_backups").resolve(backupFileName);
        assertThat(Files.exists(backupFilePath)).isTrue();
        assertThat(normalizeNewlines(Files.readString(backupFilePath)))
                .isEqualTo(normalizeNewlines(Files.readString(templateConfig())));
    }

    @Override
    protected Path templateConfig() {
        return RESOURCES_ROOT.resolve("unmigrated.yml");
    }
}
