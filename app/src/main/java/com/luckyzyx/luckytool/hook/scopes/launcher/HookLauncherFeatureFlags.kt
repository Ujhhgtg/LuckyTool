package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookLauncherFeatureFlags : YukiBaseHooker() {
    override fun onHook() {
        val twoLine = prefs(ModulePrefs).getBoolean("enable_drawer_layout_double_line_names", false)

        //Source FeatureFlags
        "com.android.launcher3.config.FeatureFlags".toClass().apply {
            method { name = "getDebugFlag";paramCount = 3 }.hook {
                before {
                    val key = args().first().string()
//                    val defValue = args(1).boolean()
//                    val description = args().last().string()
                    when (key) {
                        "ENABLE_TWOLINE_ALLAPPS" -> if (twoLine) args(1).setTrue()
                    }
                }
            }
        }
    }
}