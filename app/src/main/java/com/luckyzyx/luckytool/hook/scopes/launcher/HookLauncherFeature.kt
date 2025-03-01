package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object HookLauncherFeature : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode < 33) {
            loadHooker(HookFeatureOption)
            loadHooker(HookLauncherSettings)
        }
        if (osCode >= 34) loadHooker(HookAppFeature)
    }

    @Obfuscate
    object HookAppFeature : YukiBaseHooker() {
        override fun onHook() {
            val disableAutoSwitch =
                prefs(ModulePrefs).getBoolean("disable_auto_switch_last_task", false)

            //Source AppFeatureUtils
            "com.android.common.util.AppFeatureUtils".toClass().apply {
                //Source OplusGridRecentsConfig isEnable
                method { name = "isSupportAutoFocusToNextPageInOverviewState" }.hook {
                    if (disableAutoSwitch) replaceToFalse()
                }
            }
        }
    }

    @Obfuscate
    object HookFeatureOption : YukiBaseHooker() {
        override fun onHook() {
            val appUpdateDot = prefs(ModulePrefs).getBoolean("enable_display_app_update_dot", false)

            //Source FeatureOption
            "com.android.common.config.FeatureOption".toClass().apply {
                if (hasField { name = "isSupportAppUpdateDotSwitch" }.not()) return@apply
                method { name = "initFeature" }.hook {
                    after {
                        if (appUpdateDot) field { name = "isSupportAppUpdateDotSwitch" }.get()
                            .setTrue()
                    }
                }
            }
        }
    }

    @Obfuscate
    object HookLauncherSettings : YukiBaseHooker() {
        override fun onHook() {
            val appUpdateDot = prefs(ModulePrefs).getBoolean("enable_display_app_update_dot", false)

            //Source LauncherSettingsUtils
            "com.android.launcher.settings.LauncherSettingsUtils".toClass().apply {
                if (hasMethod { name = "isSupportAppUpdateDot" }.not()) return@apply
                method { name = "isSupportAppUpdateDot" }.hook {
                    if (appUpdateDot) replaceToTrue()
                }
            }
        }
    }
}