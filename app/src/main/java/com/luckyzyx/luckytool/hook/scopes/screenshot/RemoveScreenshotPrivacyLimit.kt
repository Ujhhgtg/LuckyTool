package com.luckyzyx.luckytool.hook.scopes.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method

object RemoveScreenshotPrivacyLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source ScreenshotContext
        "com.oplus.screenshot.screenshot.core.ScreenshotContext".toClass().apply {
            val hasOverrideScreenshotReject = hasMethod { name = "setScreenshotReject" }.not()
            method { name = "setScreenshotReject";superClass(hasOverrideScreenshotReject) }.hook {
                intercept()
            }
            val hasOverrideLongshotReject = hasMethod { name = "setLongshotReject" }.not()
            method { name = "setLongshotReject";superClass(hasOverrideLongshotReject) }.hook {
                intercept()
            }
        }
    }
}