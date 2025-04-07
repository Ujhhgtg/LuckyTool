package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class ZoomWindowConfig : YukiBaseHooker() {

    var callback: ((key: String, value: Any) -> Unit)? = null

    var mode = "0"
    val list = ArraySet<String>()

    var multiWindow = false
    var multiNum = 2

    fun loadData() {
        mode = prefs(ModulePrefs).getString("custom_app_floating_window_display_mode", "0")
        list.addAll(prefs(ModulePrefs).getStringSet("zoom_window_support_list", ArraySet()))

        dataChannel.wait<String>("custom_app_floating_window_display_mode") {
            mode = it
            YLog.debug("update zoom window configs status -> $it")
        }

        dataChannel.wait<Set<String>>("zoom_window_support_list") {
            list.clear()
            val new = prefs(ModulePrefs).getStringSet("zoom_window_support_list", ArraySet())
            list.addAll(new)
            YLog.debug("update zoom window whitelist configs -> ${list.size} | ${new.size}")
        }

        multiWindow = prefs(ModulePrefs).getBoolean("force_enable_multi_window_mode", false)
        dataChannel.wait<Boolean>("force_enable_multi_window_mode") { multiWindow = it }
        multiNum = prefs(ModulePrefs).getInt("custom_multi_window_display_upper_limit", 2)
        dataChannel.wait<Int>("custom_multi_window_display_upper_limit") { multiNum = it }

        YLog.debug("init zoom window configs success")
    }

    override fun onHook() {
        loadData()

        val osCode = getOSVersionCode
        loadHooker(HookZoomWindow())
        if (osCode >= 33) loadHooker(HookFlexibleWindow())
    }

    @Obfuscate
    inner class HookZoomWindow : YukiBaseHooker() {
        override fun onHook() {
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
                                if (list.contains(packName)) resultTrue()
                            }
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    inner class HookFlexibleWindow : YukiBaseHooker() {
        override fun onHook() {
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
                                if (list.contains(packName)) resultTrue()
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
                        if (multiWindow && multiNum > 0) result = multiNum
                    }
                }
            }
        }
    }
}