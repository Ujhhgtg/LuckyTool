package com.luckyzyx.luckytool.ui.application

import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils

@Obfuscate
class MyApplication : ModuleApplication() {
    override fun onCreate() {
        super.onCreate()
        val isBeta = BuildConfig.DEBUG || BuildConfig.VERSION_NAME.contains("beta")
        AppAnalyticsUtils.init(this, isBeta)
    }
}


























