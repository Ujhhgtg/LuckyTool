package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
@Suppress("UNCHECKED_CAST")
object ZoomWindowConfig : YukiBaseHooker() {

    override fun onHook() {
        dataChannel.wait<String>("custom_app_floating_window_display_mode") {
            HookZoomWindow.callback?.invoke("custom_app_floating_window_display_mode", it)
            HookFlexibleWindow.callback?.invoke("custom_app_floating_window_display_mode", it)
        }
        dataChannel.wait<Set<String>>("zoom_window_support_list") {
            HookZoomWindow.callback?.invoke("zoom_window_support_list", it)
            HookFlexibleWindow.callback?.invoke("zoom_window_support_list", it)
        }
        val osCode = getOSVersionCode
        loadHooker(HookZoomWindow)
        if (osCode >= 33) loadHooker(HookFlexibleWindow)
    }

    @Obfuscate
    object HookZoomWindow : YukiBaseHooker() {
        var callback: ((key: String, value: Any) -> Unit)? = null

        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("custom_app_floating_window_display_mode", "0")
            var supportList =
                prefs(ModulePrefs).getStringSet("zoom_window_support_list", ArraySet())
            callback = { key: String, value: Any ->
                when (key) {
                    "custom_app_floating_window_display_mode" -> mode = value as String
                    "zoom_window_support_list" -> supportList = value as Set<String>
                }
            }

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
        }
    }

    @Obfuscate
    object HookFlexibleWindow : YukiBaseHooker() {
        var callback: ((key: String, value: Any) -> Unit)? = null

        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("custom_app_floating_window_display_mode", "0")
            var supportList =
                prefs(ModulePrefs).getStringSet("zoom_window_support_list", ArraySet())
            var multiWindow = prefs(ModulePrefs).getBoolean("enable_multi_window_mode", false)
            dataChannel.wait<Boolean>("enable_multi_window_mode") { multiWindow = it }
            callback = { key: String, value: Any ->
                when (key) {
                    "custom_app_floating_window_display_mode" -> mode = value as String
                    "zoom_window_support_list" -> supportList = value as Set<String>
                }
            }

            //Source FlexibleWindowUtils
            "com.android.server.wm.FlexibleWindowUtils".toClassOrNull()?.apply {
                method {
                    name = "isSupportFlexibleWindow"
                    param(StringClass, StringClass)
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

            //Source FlexibleWindowManagerService
            "com.android.server.wm.FlexibleWindowManagerService".toClassOrNull()?.apply {
                method {
                    name = "getMaxWinNum"
                    returnType = IntType
                }.hook {
                    after {
                        if (multiWindow) result = 1000
                    }
                }
            }
        }
    }
}