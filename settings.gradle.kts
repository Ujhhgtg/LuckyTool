enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
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
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
    }
}

rootProject.name = "LuckyTool"
include(":app", ":hidden-api-stub")
