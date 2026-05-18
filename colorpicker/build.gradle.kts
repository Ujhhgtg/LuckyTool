plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.luckyzyx.colorpicker"
    compileSdk {
        version = release(rootProject.extra.get("compileSdkVersion") as Int) {
//            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = rootProject.extra.get("minSdkVersion") as Int
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(rootProject.extra.get("jdkVersion") as Int)
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.preference.ktx)
}