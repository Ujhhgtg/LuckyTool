import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.lsplugin.lsparanoid) apply false
}

buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}

tasks {
    register("clean", Delete::class) {
        delete(layout.buildDirectory)
    }
}

subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }
        }
    }
}