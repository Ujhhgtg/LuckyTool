plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin)
}

android {
    compileSdk = 36
    namespace = "com.android.internal"
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}
