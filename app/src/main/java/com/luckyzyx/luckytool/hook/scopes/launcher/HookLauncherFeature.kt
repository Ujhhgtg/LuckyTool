package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

object HookLauncherFeature : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        loadHooker(HookFeatureOption)
        loadHooker(HookLauncherSettings)
        if (osCode >= 34) loadHooker(HookAppFeature)
    }

    @Obfuscate
    object HookAppFeature : YukiBaseHooker() {
        override fun onHook() {
            val disableAutoSwitch =
                prefs(ModulePrefs).getBoolean("disable_auto_switch_last_task", false)

            //Source AppFeatureUtils
            "com.android.common.util.AppFeatureUtils".toClass().resolve().apply {
                //Source OplusGridRecentsConfig isEnable
                firstMethod { name = "isSupportAutoFocusToNextPageInOverviewState" }.hook {
                    if (disableAutoSwitch) replaceToFalse()
                }
            }
        }
    }

    @Obfuscate
    object HookFeatureOption : YukiBaseHooker() {
        override fun onHook() {
            val appUpdateDot = prefs(ModulePrefs).getBoolean("enable_display_app_update_dot", false)
            val disableDockerMax =
                prefs(ModulePrefs).getBoolean("remove_docker_max_number_limit", false)

            //Source FeatureOption
            "com.android.common.config.FeatureOption".toClass().resolve().apply {
                firstMethod { name = "initFeature" }.hook {
                    after {
                        if (appUpdateDot) {
                            firstFieldOrNull { name = "isSupportAppUpdateDotSwitch" }?.set(true)
                        }
                    }
                }
                if (disableDockerMax) {
                    firstMethodOrNull { name = "isDockerMax5" }?.hook {
                        replaceToFalse()
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
            "com.android.launcher.settings.LauncherSettingsUtils".toClass().resolve().apply {
                if (appUpdateDot) {
                    firstMethodOrNull { name = "isSupportAppUpdateDot" }?.hook {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}