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

package io.github.madethoughts.mayflower.plugin.yaml;

import io.github.madethoughts.mayflower.plugin.McPlugin;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 Generates a plugin.yml from the {@link McPlugin} annotation.
 Values specified in the "template-plugin.yml" override generated ones.
 */
public class McPluginVisitor implements TypeElementVisitor<McPlugin, Object> {

    private static final String PLUGIN_YAML = "plugin.yml";
    private static final String TEMPLATE_PLUGIN_YAML = "override.plugin.yml";
    private static final BiPredicate<Path, BasicFileAttributes> TEMPLATE_PLUGIN_YAML_PREDICATE =
            (path, basicFileAttributes) -> path.endsWith(Path.of("resources", TEMPLATE_PLUGIN_YAML));
    private final Yaml yaml = new Yaml();

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        var projectDir = context.getProjectDir().orElseThrow();

        // search for existing file
        var data = find(projectDir).findFirst()
                .map(McPluginVisitor::readString)
                .map(s -> new Yaml().<Map<String, Object>>load(s)).orElseGet(HashMap::new);

        @SuppressWarnings("DataFlowIssue") // annotation should not be null
        var pluginValues = element.getAnnotation(McPlugin.class).getValues().entrySet();
        data.computeIfAbsent("main", s -> element.getName());

        for (var entry : pluginValues) {
            var key = entry.getKey().toString();
            var value = entry.getValue();
            var mappedKey = switch (key) {
                case "apiVersion" -> {
                    // bring enum entry in right format, the enum is already a string here.
                    value = value.toString().replace("V", "").replace("_", ".");
                    yield "api-version";
                }
                case "authors" -> {
                    if (value instanceof String[] authors && 1 == authors.length) {
                        value = authors[0];
                        yield "author";
                    }
                    yield "authors";
                }
                default -> key;
            };

            data.putIfAbsent(mappedKey, value);
        }

        var ouputPath = context.getClassesOutputPath().orElseThrow().resolve(PLUGIN_YAML);
        var dump = yaml.dump(data);
        writeString(ouputPath, dump);
    }

    private static Stream<Path> find(Path start) {
        try {
            // 6 should be enough depth based on the gradle file hierarchy
            return Files.find(start, 6, TEMPLATE_PLUGIN_YAML_PREDICATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeString(Path path, String text) {
        try {
            Files.writeString(path, text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
