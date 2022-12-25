plugins {
    java
    id("io.micronaut.library") version "3.5.1"
}

group = "io.github.madethougth"
version = "0.1"
description = "mayflower"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java")

    implementation("io.micronaut", "micronaut-inject-java")
    implementation("io.micronaut", "micronaut-runtime")

    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    register<tequila.ValidateCommitsGitTask>("validateCommits")
    register<tequila.ValidateCommitMessageGitTask>("validateCommitMessageGit")
    register<tequila.InstallGitHook>("installGitHook")

    register("setup") {
        dependsOn("installGitHook")
    }

    assemble {
        dependsOn("setup") // auto install git hook when project is built
    }
}