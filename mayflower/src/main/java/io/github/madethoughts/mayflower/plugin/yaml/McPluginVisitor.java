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

import io.github.madethoughts.mayflower.internal.FileUtils;
import io.github.madethoughts.mayflower.plugin.McPlugin;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 Generates a plugin.yml from the {@link McPlugin} annotation.
 Values specified in the "template-plugin.yml" override generated ones.
 */
public class McPluginVisitor implements TypeElementVisitor<McPlugin, Object> {

    private static final String PLUGIN_YAML = "plugin.yml";
    private static final String TEMPLATE_PLUGIN_YAML = "override.plugin.yml";
    private static final BiPredicate<Path, BasicFileAttributes> TEMPLATE_PLUGIN_YAML_PREDICATE =
            (path, basicFileAttributes) -> path.endsWith(Path.of("resources", TEMPLATE_PLUGIN_YAML));
    public static final String INNER_CLASS_ERR = "@McPlugin is not permitted on inner classes";
    private final Yaml yaml = new Yaml();

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        // skip inner classes, the root class have to be annotated with this annotation
        if (element.isInner()) {
            if (element.hasAnnotation(McPlugin.class)) {
                context.fail(INNER_CLASS_ERR, element);
            }
            return;
        }
        var projectDir = context.getProjectDir().orElseThrow();

        // search for existing file
        var data = FileUtils.find(projectDir, 6, TEMPLATE_PLUGIN_YAML_PREDICATE).findFirst()
                                             .map(FileUtils::readString)
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
        FileUtils.writeString(ouputPath, dump);
    }
}
