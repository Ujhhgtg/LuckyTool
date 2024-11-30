import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
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