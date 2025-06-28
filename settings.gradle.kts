enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        maven("https://maven.aliyun.com/repository/releases")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/apache-snapshots")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal {
            content { includeGroup("com.highcapable.flexiui") }
            content { includeGroup("com.highcapable.yukihookapi") }
            content { includeGroup("com.highcapable.kavaref") }
            content { includeGroup("io.github.libxposed") }
            content { includeGroup("id.dhd") }
        }
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        maven("https://maven.aliyun.com/repository/releases")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/apache-snapshots")
        google()
        mavenCentral()
    }
}

rootProject.name = "LuckyTool"
include(":app", ":hidden-api-stub")
