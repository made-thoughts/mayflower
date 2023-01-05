rootProject.name = "mayflower"
include("test_plugin")
include("mayflower")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper", "io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
        }
    }
}