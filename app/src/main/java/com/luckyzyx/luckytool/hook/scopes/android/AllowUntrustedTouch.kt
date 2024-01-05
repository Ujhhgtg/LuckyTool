package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object AllowUntrustedTouch : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("allow_untrusted_touch", false)

        //Source UntrustedTouchController
        "com.android.server.input.UntrustedTouchController".toClass().apply {
            method { name = "isOplusTrustedApp" }.hook {
                if (isEnable) replaceToTrue()
            }
            method { name = "showTipsDialog" }.hook {
                if (isEnable) intercept()
            }
        }
        //Source WindowStateExtImpl
        "com.android.server.wm.WindowStateExtImpl".toClass().apply {
            method { name = "isOplusTrustedWindow" }.hook {
                if (isEnable) replaceToTrue()
            }
        }
        if (SDK >= A14) return
        //Source InputManager
        "android.hardware.input.InputManager".toClass().apply {
            method { name = "getBlockUntrustedTouchesMode" }.hook {
                if (isEnable) replaceTo(0)
            }
        }
    }
}