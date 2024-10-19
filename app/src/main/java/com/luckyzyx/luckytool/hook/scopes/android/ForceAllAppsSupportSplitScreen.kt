package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object ForceAllAppsSupportSplitScreen : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("force_all_apps_support_split_screen", false)
        dataChannel.wait<Boolean>("force_all_apps_support_split_screen") { isEnable = it }

        //Source OplusSplitScreenManagerService
        "com.android.server.wm.OplusSplitScreenManagerService".toClass().apply {
            method {
                name = "supportsSplitScreenByVendorPolicy"
                paramCount(3..4)
            }.hook {
                before {
                    if (!isEnable) return@before
                    val packageName = args().first().string()
                    val activityName = args(1).string()
//                    val candidate = args(2).boolean()

                    if (packageName.isBlank()) return@before

                    val isSafeSenterUI = method {
                        name = "isSafeSenterUI";paramCount = 1
                    }.get(instance).boolean(activityName)
                    if (isSafeSenterUI) return@before

                    if (method.parameterCount == 4) {
                        val userId = args().last().int()
                        val isHidenPackage = method {
                            name = "isHidenPackage";paramCount = 2
                        }.get(instance).boolean(packageName, userId)
                        if (isHidenPackage) return@before
                    }

                    resultTrue()
                }
            }
        }
    }
}