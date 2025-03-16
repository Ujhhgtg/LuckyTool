enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal {
            content { includeGroup("com.highcapable.flexiui") }
            content { includeGroup("com.highcapable.yukihookapi") }
            content { includeGroup("io.github.libxposed") }
            content { includeGroup("id.dhd") }
        }
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        google()
        mavenCentral()
    }
}

rootProject.name = "LuckyTool"
include(":app", ":hidden-api-stub", ":selector")
include(":common-utils")
