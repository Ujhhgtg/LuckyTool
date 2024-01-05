package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object ScrollToTopWhiteList : YukiBaseHooker() {
    override fun onHook() {
        val mode = prefs(ModulePrefs).getString("set_click_statusbar_scroll_to_top_mode", "0")

        //Source OplusScrollToTopRusHelper -> OplusScrollToTopSystemManager
        "com.android.server.OplusScrollToTopRusHelper".toClass().apply {
            if (hasMethod { name = "isInWhiteList" }) method { name = "isInWhiteList" }.hook {
                before {
                    when (mode) {
                        "1" -> resultFalse()
                        "2" -> resultTrue()
                    }
                }
            }
        }
    }
}