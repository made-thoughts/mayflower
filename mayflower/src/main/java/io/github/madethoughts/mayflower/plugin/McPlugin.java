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

package io.github.madethoughts.mayflower.plugin;

import jakarta.inject.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 This annotation indicates that a class is the plugin's entrypoint, the class must also extend {@link MayflowerPlugin}
 <p>
 It is also used to generate a "plugin.yml".
 */
@Singleton
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface McPlugin {
    /**
     @return The plugin's name
     */
    String name();

    /**
     @return The plugin's version
     */
    String version();

    /**
     @return The plugin's api version
     */
    ApiVersion apiVersion();

    /**
     @return The plugin's authors. The field "author" is automatically set if only one author is given.
     */
    String[] authors();

    /**
     @return The plugin's description
     */
    String description() default "";

    /**
     @return The plugin's loading time
     */
    Load load() default Load.POSTWORLD;

    /**
     @return The plugin's website
     */
    String website() default "";

    /**
     @return The plugin's plugin dependencies
     */
    String[] depend() default "";

    /**
     @return The plugin's plugin soft dependencies
     */
    String[] softdepend() default "";

    /**
     @return The plugins prefix
     */
    String prefix() default "";

    /**
     @return The plugins that should be loaded before this plugin.
     */
    String[] loadbefore() default "";

    /**
     Defines the possible plugin load times.
     */
    enum Load {
        /**
         load: STARTUP
         */
        STARTUP,
        /**
         load: POSTWORLD
         */
        POSTWORLD
    }

    /**
     This enum defines api versions applicable in {@link McPlugin#apiVersion()}
     */
    enum ApiVersion {
        /**
         api-version: 1.13
         */
        V1_13,
        /**
         api-version: 1.14
         */
        V1_14,
        /**
         api-version: 1.15
         */
        V1_15,
        /**
         api-version: 1.16
         */
        V1_16,
        /**
         api-version: 1.17
         */
        V1_17,
        /**
         api-version: 1.18
         */
        V1_18,
        /**
         api-version: 1.19
         */
        V1_19
    }
}
