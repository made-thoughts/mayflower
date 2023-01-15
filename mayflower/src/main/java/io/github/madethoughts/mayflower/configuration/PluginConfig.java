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

import io.micronaut.context.annotation.AliasFor;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.AccessorsStyle;
import io.micronaut.core.version.annotation.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 This annotation indicates, that this configuration is the plugin's config. The values for this config will also be
 loaded from the "config.yml" file in the plugins' directory. Also, all entries of this configuration need a default
 value, for that {@link Default} can be used. The corresponding "config.yml" will have default values based on them.
 <p>
 All {@link PluginConfig} implementations must have a {@link Version} annotation. The version format is "minor.major"
 and "number.number". The semantics are equal to schematic versioning.
 <p>
 Major version bumps should only be made if
 breaking changes are applied, for example removing an option, renaming or changing the purpose. For every major
 version update a {@link ConfigMigration} must be provided.
 <p>
 Minor version bumps should only be made if non-breaking changes are applied, for example adding a new option or
 changing the default value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@ConfigurationProperties(PluginConfig.PLUGIN_PREFIX)
@AccessorsStyle(readPrefixes = "")
public @interface PluginConfig {

    /**
     The prefix used to store plugin config properties
     */
    String PLUGIN_PREFIX = "plugin";

    // @formatter:off
    /**
     The value field holds the subcategory of the plugin config. "" (default) means the root config "plugin".
     <blockquote><pre>
     &#64;PluginConfig
     class PluginConfigFile {
         &#64;PluginConfig("foo")
         class FooSection {
             // entries
         }
     }
     </pre></blockquote>
     In this example FooSection corresponds to "plugin.foo".
     @return The subcategory
     */
    // @formatter:on
    @AliasFor(annotation = ConfigurationProperties.class, member = "value")
    String value() default "";

    /**
     @return the version of this config, this field must be set at the root level
     */
    double version() default -1;

    /**
     The value of this field will be mapped to {@link AccessorsStyle#readPrefixes()}

     @return The read prefix
     */
    @AliasFor(annotation = AccessorsStyle.class, member = "readPrefixes")
    String[] readPrefixes() default "";
}
