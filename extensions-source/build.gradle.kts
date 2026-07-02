pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "custom-extensions"

// This completely locks the build to ONLY look at your extension!
include(":src:en:ishallmasterthisfamily")