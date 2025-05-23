plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("org.eclipse.jgit", "org.eclipse.jgit", "6.10.1.202505221210-r")
}