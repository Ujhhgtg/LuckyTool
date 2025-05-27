package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
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
            val disableDockerMax =
                prefs(ModulePrefs).getBoolean("remove_docker_max_number_limit", false)

            //Source FeatureOption
            "com.android.common.config.FeatureOption".toClass().apply {
                val hasAppDotSwitch = hasField { name = "isSupportAppUpdateDotSwitch" }
                method { name = "initFeature" }.hook {
                    after {
                        if (hasAppDotSwitch && appUpdateDot) field {
                            name = "isSupportAppUpdateDotSwitch"
                        }.get().setTrue()
                    }
                }
                val hasDockerMax = hasMethod { name = "isDockerMax5" }
                if (hasDockerMax && disableDockerMax) {
                    method { name = "isDockerMax5" }.hook {
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
            "com.android.launcher.settings.LauncherSettingsUtils".toClass().apply {
                val hasAppDotSwitch = hasField { name = "isSupportAppUpdateDotSwitch" }
                if (hasAppDotSwitch && appUpdateDot) {
                    method { name = "isSupportAppUpdateDot" }.hook {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}