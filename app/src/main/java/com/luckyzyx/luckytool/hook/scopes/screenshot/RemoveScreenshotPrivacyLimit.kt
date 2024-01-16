package com.luckyzyx.luckytool.hook.scopes.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveScreenshotPrivacyLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source ScreenshotContext
        "com.oplus.screenshot.screenshot.core.ScreenshotContext".toClass().apply {
            method { name = "setScreenshotReject" }.hook {
                intercept()
            }
            method { name = "setLongshotReject" }.hook {
                intercept()
            }
        }
    }
}