package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookLauncherFeature : YukiBaseHooker() {
    override fun onHook() {
        val appUpdateDot = prefs(ModulePrefs).getBoolean("enable_display_app_update_dot", false)

        //Source FeatureOption
        "com.android.common.config.FeatureOption".toClass().apply {
            if (hasField { name = "isSupportAppUpdateDotSwitch" }.not()) return@apply
            method { name = "initFeature" }.hook {
                after {
                    if (appUpdateDot) field { name = "isSupportAppUpdateDotSwitch" }.get().setTrue()
                }
            }
        }

        //Source LauncherSettingsUtils
        "com.android.launcher.settings.LauncherSettingsUtils".toClass().apply {
            if (hasMethod { name = "isSupportAppUpdateDot" }.not()) return@apply
            method { name = "isSupportAppUpdateDot" }.hook {
                if (appUpdateDot) replaceToTrue()
            }
        }
    }
}