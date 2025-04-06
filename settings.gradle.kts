pluginManagement {
    repositories {
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        // Currently needed for Intermediary and other temporary dependencies
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5.2"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5")
        vcsVersion = "1.21.5"
    }
}
