// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.lsplugin.lsparanoid) apply false
}

extra["compileSdkVersion"] = 36
extra["targetSdkVersion"] = 28
extra["minSdkVersion"] = 30

extra["jdkVersion"] = 21

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