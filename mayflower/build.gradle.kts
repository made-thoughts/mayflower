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

plugins {
    id("io.micronaut.library") version "3.7.0"
}

group = "io.github.madethougth"
version = "0.1"
description = "mayflower"

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java")

    api("io.micronaut", "micronaut-inject-java")
    api("io.micronaut", "micronaut-context")

    compileOnly(libs.paper)

    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testAnnotationProcessor(project(":mayflower"))
    testAnnotationProcessor(libs.paper)

    testImplementation(libs.paper)
    testImplementation("com.github.seeseemelk", "MockBukkit-v1.19", "2.144.4")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("org.assertj", "assertj-core", "3.22.0")
    testImplementation("ch.qos.logback", "logback-classic", "1.4.5")
    testImplementation("com.gitlab.taucher2003.t2003-utils", "log", "1.1-alpha.20")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
