package com.luckyzyx.luckytool.ui.application

import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils.init

@Obfuscate
class MyApplication : ModuleApplication() {
    override fun onCreate() {
        super.onCreate()
        init(BuildConfig.DEBUG || BuildConfig.VERSION_NAME.contains("beta"))
    }
}


























