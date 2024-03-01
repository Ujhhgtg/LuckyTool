import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore/keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.joom.paranoid")
}

android {
    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = null
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    compileSdk = 34
    namespace = "com.luckyzyx.luckytool"
    defaultConfig {
        applicationId = "com.luckyzyx.luckytool"
        minSdk = 30
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = getVersionCode()
        versionName = "1.2.2_beta"
        buildConfigField("String", "APP_CENTER_SECRET", getAppCenterSecret())
        buildConfigField("String", "APP_CENTER_SECRET_BETA", getAppCenterSecret(true))
        ndk.abiFilters.addAll(arrayOf("arm64-v8a"/*, "armeabi-v7a", "x86", "x86_64"*/))
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
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
}

dependencies {
    //implementation(fileTree("libs").include("*.jar"))

    compileOnly(project(":hidden-api-stub"))
    //Xposed基础
    compileOnly("de.robv.android.xposed:api:82")
    //YukiHookAPI
//    implementation("com.highcapable.yukireflection:api:1.0.2")
    //noinspection GradleDependency
    implementation("com.highcapable.yukihookapi:api:1.2.0-fix")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.2.0")
//    implementation(files("libs/yukihookapi-release.jar"))
//    ksp files("libs/yukihookapi-ksp-xposed-1.1.5-beta2.jar")

    //Dexkit
    implementation("org.luckypray:dexkit:2.0.0")
    //MMKV
    implementation("com.tencent:mmkv:1.3.3")

    //BetterAndroid
//    implementation("com.highcapable.flexiui:flexiui-core:0.0.1")
    implementation("com.highcapable.betterandroid:ui-component:1.0.4")
//    implementation("com.highcapable.betterandroid:ui-extension:1.0.0")
//    implementation("com.highcapable.betterandroid:system-extension:1.0.0")

    //Material主题
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    //约束布局
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //快速创建Settings
    implementation("androidx.preference:preference-ktx:1.2.1")
    //下拉刷新控件
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    // 权限请求框架
    implementation("com.github.getActivity:XXPermissions:18.6")
    //崩溃日志显示
    implementation("com.github.simplepeng.SpiderMan:spiderman:v1.1.9")
    //滚动条
    implementation("me.zhanghai.android.fastscroll:library:1.3.0")
    //Color Picker
    implementation("io.github.vadiole:colorpicker:1.0.4")
    //kotlin协程
    val kotlinxCoroutinesVersion = "1.8.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${kotlinxCoroutinesVersion}")
    //Net OkHttp相关
    //noinspection GradleDependency
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.liangjingkanji:Net:3.6.4")
    //libsu
    val libsuVersion = "5.2.2"
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")
    implementation("com.github.topjohnwu.libsu:service:${libsuVersion}")
//    implementation("com.github.topjohnwu.libsu:nio:${libsuVersion}")

    //Microsoft AppCenter
    val appCenterSdkVersion = "5.0.4"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")

    //MarkDown
    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion")
    implementation("io.noties.markwon:image:$markwonVersion")
    implementation("io.noties.markwon:ext-tables:$markwonVersion")
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
    } else throw GradleException("无法读取 version.properties!")
}

fun getAppCenterSecret(isBeta: Boolean = false): String {
    val file = rootProject.file("keystore/app_center_secret")
    val list = if (file.exists()) file.readLines() else return ""
    if (list.size != 2) return ""
    return if (isBeta) list.lastOrNull() ?: ""
    else list.firstOrNull() ?: ""
}