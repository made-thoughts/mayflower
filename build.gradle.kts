plugins {
    java
}


allprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenCentral()
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