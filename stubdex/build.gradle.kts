plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "com.luckyzyx.luckytool.stubdex"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.luckyzyx.luckytool.stubdex"
        minSdk = 30
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
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