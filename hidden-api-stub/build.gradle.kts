plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin)
}

android {
    compileSdk = 34
    namespace = "com.android.internal"
    java {
        toolchain { languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion) }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}
