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
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}


dependencies {
    annotationProcessor(project(":mayflower"))
    implementation(project(":mayflower"))

    compileOnly(libs.paper)
}

tasks {
    shadowJar {
        dependencies {
            exclude(libs.paper.toString())
        }
        mergeServiceFiles()
    }

    runServer {
        minecraftVersion("1.19.3")
    }
}