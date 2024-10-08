import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore/keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.lsplugin.apksign)
    alias(libs.plugins.lsplugin.resopt)
    id("com.joom.paranoid")
}

apksign {
    storeFileProperty = keystoreProperties["storeFile"] as String
    storePasswordProperty = keystoreProperties["storePassword"] as String
    keyAliasProperty = keystoreProperties["keyAlias"] as String
    keyPasswordProperty = keystoreProperties["keyPassword"] as String
}

android {
    compileSdk = 35
    namespace = "com.luckyzyx.luckytool"
    defaultConfig {
        applicationId = "com.luckyzyx.luckytool"
        minSdk = 30
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = getVersionCode()
        versionName = "1.2.5_beta"
        buildConfigField("String", "APP_CENTER_SECRET", getAppCenterSecret())
        buildConfigField("String", "APP_CENTER_SECRET_BETA", getAppCenterSecret(true))
        ndk.abiFilters.addAll(arrayOf("arm64-v8a"/*, "armeabi-v7a", "x86", "x86_64"*/))
    }

    signingConfigs {
        all {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = null
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    java {
        toolchain { languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion) }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        aidl = true
        viewBinding = true
        buildConfig = true
    }
    applicationVariants.all {
        val buildType = buildType.name
        val version = "$versionName($versionCode)"
        println("buildVersion -> $version ($buildType)")
        outputs.all {
            @Suppress("DEPRECATION")
            if (this is com.android.build.gradle.api.ApkVariantOutput) {
                if (buildType == "release") outputFileName = "LuckyTool_v${version}.apk"
                if (buildType == "debug") outputFileName = "LuckyTool_v${version}_debug.apk"
                println("outputFileName -> $outputFileName")
            }
        }
    }
    androidResources.additionalParameters.addAll(
        arrayOf("--allow-reserved-package-id", "--package-id", "0x64")
    )
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "META-INF/**"
            excludes += "okhttp3/**"
            excludes += "kotlin/**"
            excludes += "**.properties"
            excludes += "**.bin"
            excludes += "kotlin-tooling-metadata.json"
        }
    }
}

dependencies {
    compileOnly(project(":hidden-api-stub"))

    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi)
    ksp(libs.ksp.yukihookapi)
    implementation(libs.dexkit)

    implementation(libs.mmkv)

    implementation(libs.betterandroid.ui.component)
    implementation(libs.material)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.xxpermissions)
    implementation(libs.spiderman)
    implementation(libs.fastscroll)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.okhttp)
    implementation(libs.net)

    implementation(libs.libsu.core)
    implementation(libs.libsu.service)
    implementation(libs.libsu.io)

    implementation(libs.appcenter.analytics)
    implementation(libs.appcenter.crashes)

    implementation(libs.markwon.core)
    implementation(libs.markwon.html)
    implementation(libs.markwon.image)
    implementation(libs.markwon.ext.tables)

    implementation(libs.android.image.cropper)
    implementation(libs.colorpicker)
}

fun getVersionCode(): Int {
    val propsFile = file("version.properties")
    if (propsFile.canRead()) {
        val properties = Properties()
        properties.load(FileInputStream(propsFile))
        var vCode = properties["versionCode"].toString().toInt()
        properties["versionCode"] = (++vCode).toString()
        properties.store(propsFile.writer(), null)
        println("versionCode -> $vCode")
        return vCode
    } else throw GradleException("Can't read version.properties!")
}

fun getAppCenterSecret(isBeta: Boolean = false): String {
    val file = rootProject.file("keystore/app_center_secret")
    if (file.canRead()) {
        val list = if (file.exists()) file.readLines() else return ""
        if (list.size != 2) return ""
        return if (isBeta) list.lastOrNull() ?: ""
        else list.firstOrNull() ?: ""
    } else throw GradleException("Can't read app center keystore!")
}