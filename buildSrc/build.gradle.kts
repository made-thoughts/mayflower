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
    implementation("org.eclipse.jgit", "org.eclipse.jgit", "6.4.0.202211300538-r")
}