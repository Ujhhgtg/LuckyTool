package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.A15
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object ZoomWindowConfig : YukiBaseHooker() {
    override fun onHook() {
        var mode = prefs(ModulePrefs).getString("custom_app_floating_window_display_mode", "0")
        dataChannel.wait<String>("custom_app_floating_window_display_mode") { mode = it }
        var supportList = prefs(ModulePrefs).getStringSet("zoom_window_support_list", ArraySet())
        dataChannel.wait<Set<String>>("zoom_window_support_list") { supportList = it }

        //Source OplusZoomWindowConfig
        "com.android.server.wm.OplusZoomWindowConfig".toClass().apply {
            method {
                name = "isSupportZoomMode"
                param(StringClass, IntType, StringClass, BundleClass)
            }.hook {
                before {
                    when (mode) {
                        "1" -> resultFalse()
                        "2" -> resultTrue()
                        "3" -> {
                            val target = args().first().string()
                            val packName = if (target.contains("/").not()) target
                            else target.split("/")[0]
                            if (supportList.contains(packName)) resultTrue()
                        }
                    }
                }
            }
        }

        if (SDK < A15) return

        //Source FlexibleTaskController C15
        "com.android.server.wm.FlexibleTaskController".toClassOrNull()?.apply {
            val hasSupport = hasMethod { name = "isSupportFlexibleWindow" }
            if (hasSupport) method {
                name = "isSupportFlexibleWindow"
                param(StringClass, IntType, StringClass, BundleClass)
            }.hook {
                before {
                    when (mode) {
                        "1" -> resultFalse()
                        "2" -> resultTrue()
                        "3" -> {
                            val target = args().first().string()
                            val packName = if (target.contains("/").not()) target
                            else target.split("/")[0]
                            if (supportList.contains(packName)) resultTrue()
                        }
                    }
                }
            }
        }
    }
}