import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore/keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.lsplugin.apksign)
    alias(libs.plugins.lsplugin.resopt)
    alias(libs.plugins.lsplugin.lsparanoid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.navigation.safe.args)
}

apksign {
    storeFileProperty = keystoreProperties["storeFile"] as String
    storePasswordProperty = keystoreProperties["storePassword"] as String
    keyAliasProperty = keystoreProperties["keyAlias"] as String
    keyPasswordProperty = keystoreProperties["keyPassword"] as String
}

lsparanoid {
    includeDependencies = false
    variantFilter = { variant -> variant.name == "release" }
}

android {
    namespace = "com.luckyzyx.luckytool"
    compileSdk {
        version = release(36)
    }
    defaultConfig {
        applicationId = "com.luckyzyx.luckytool"
        minSdk = 30
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = getVersionCode()
        versionName = "1.3.4"
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
            //noinspection NotShrinkingResources
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
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
    }
}

dependencies {
    compileOnly(projects.hiddenApiStub)
    implementation(projects.colorpicker)

    //XPosed or API
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi)
    ksp(libs.ksp.yukihookapi)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    implementation(libs.dexkit)

    //AndroidX
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //Android UI
    implementation(libs.material)
    implementation(libs.betterandroid.ui.component)
    implementation(libs.betterandroid.ui.component.adapter)
    implementation(libs.betterandroid.ui.extension)
    implementation(libs.betterandroid.system.extension)
    implementation(libs.hikage.core)
    implementation(libs.hikage.compiler)
    implementation(libs.hikage.extension)
    implementation(libs.hikage.extension.betterandroid)
    implementation(libs.hikage.widget.androidx)
    implementation(libs.hikage.widget.material)

    //KotlinX
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)

    //OkHttp3
    implementation(libs.okhttp3)
    implementation(libs.net)

    //LibSU
    implementation(libs.libsu.core)
    implementation(libs.libsu.service)
    implementation(libs.libsu.io)

    //MarkDown
    implementation(libs.markwon.core)
    implementation(libs.markwon.html)
    implementation(libs.markwon.image)
    implementation(libs.markwon.ext.tables)

    //Tools
    implementation(libs.deviceCompat)
    implementation(libs.xxpermissions)
    implementation(libs.spiderman)
    implementation(libs.fastscroll)
    implementation(libs.android.image.cropper)
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