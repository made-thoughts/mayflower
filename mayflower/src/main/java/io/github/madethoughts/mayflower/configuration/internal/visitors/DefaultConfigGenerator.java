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

package io.github.madethoughts.mayflower.configuration.internal.visitors;

import io.github.madethoughts.mayflower.configuration.PluginConfig;
import io.github.madethoughts.mayflower.configuration.internal.Configs;
import io.github.madethoughts.mayflower.internal.FileUtils;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.MemberElement;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 This TypeElementVisitors generates a default config at compile time, consuming interfaces annotated with
 {@link PluginConfig}.
 */

// service
@SuppressWarnings("unused")
public class DefaultConfigGenerator implements TypeElementVisitor<PluginConfig, Object> {

    /**
     The error message printed when the default value annotation is missing
     */
    public static final String MISSING_DEFAULT_VALUE_ERR =
            "All members of @PluginConfig classes must be annotated with @Bindable(defaultValue = ...) or @Default(...)";
    /**
     The error message printed when the version isn't specified
     */
    public static final String MISSING_VERSION_ERR =
            "@PluginConfig on root interface need the PluginConfig#version field set to a positive number!";

    /**
     The error message printed when unequal versions are found
     */
    public static final String UNEQUAL_VERSIONS_ERR = "Unequal versions found: %s and %s";
    private final Collection<MethodElement> alreadyInspected = new HashSet<>();
    private final YamlConfiguration defaultValues = new YamlConfiguration();

    private static String configPath(MemberElement element) {
        var sections = new ArrayList<String>();
        sections.add(element.getName());

        var currentElement = element.getDeclaringType();
        while (isInner(currentElement)) {
            currentElement.stringValue(PluginConfig.class).ifPresent(sections::add);
            currentElement = currentElement.getEnclosingType().orElseThrow();
        }

        Collections.reverse(sections);
        return String.join(".", sections);
    }

    private static boolean isInner(ClassElement element) {
        return element.getEnclosingType()
                      .filter(outer -> outer.hasAnnotation(PluginConfig.class))
                      .isPresent();
    }

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        // skip version validation if class is not root nor @PluginConfig class
        if (!element.hasAnnotation(PluginConfig.class) || isInner(element)) return;

        // markt class as config root
        var rootValue = AnnotationValue.builder(element.getAnnotation(PluginConfig.class), RetentionPolicy.RUNTIME)
                                  .member("root", true)
                                  .build();
        element.annotate(rootValue);

        var version = Configs.version(element);
        if (0 > version) {
            context.fail(MISSING_VERSION_ERR, element);
            return;
        }
        if (!defaultValues.contains("version")) defaultValues.set("version", version);

        var configVersion = defaultValues.getDouble("version");
        if (configVersion != version) {
            context.fail(UNEQUAL_VERSIONS_ERR.formatted(configVersion, version), element);
        }
    }

    @Override
    public void visitMethod(MethodElement element, VisitorContext context) {
        if (!alreadyInspected.add(element)) return; // skip already inspected methods

        // don't inspect references to other configs
        if (element.getReturnType().hasAnnotation(PluginConfig.class)) return;

        element.getValue(Bindable.class, "defaultValue")
               .ifPresentOrElse(defaultVal -> defaultValues.set(configPath(element), defaultVal),
                       () -> context.fail(MISSING_DEFAULT_VALUE_ERR, element)
               );
    }

    @Override
    public void finish(VisitorContext visitorContext) {
        var outputPath = visitorContext.getClassesOutputPath().orElseThrow()
                                       .resolve("default-config.yml");
        FileUtils.writeString(outputPath, defaultValues.saveToString());
    }
}
