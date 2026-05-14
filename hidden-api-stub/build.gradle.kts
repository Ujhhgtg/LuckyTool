plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.luckyzyx.internal"
    compileSdk {
        version = release(36)
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.androidx.annotation)
}
