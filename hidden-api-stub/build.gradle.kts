plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
