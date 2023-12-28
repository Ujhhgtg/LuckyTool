package com.luckyzyx.luckytool.hook.scope.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

@Suppress("UNUSED_VARIABLE")
object ForceAllAppsSupportSplitScreen : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("force_all_apps_support_split_screen", false)
        dataChannel.wait<Boolean>("force_all_apps_support_split_screen") { isEnable = it }

        //Source OplusSplitScreenManagerService
        "com.android.server.wm.OplusSplitScreenManagerService".toClass().apply {
            method { name = "supportsSplitScreenByVendorPolicy";paramCount = 3 }.hook {
                before {
                    if (!isEnable) return@before
                    val packageName = args().first().string()
                    val activityName = args(1).string()
                    val candidate = args().last().boolean()
                    val isSafeSenterUI = method {
                        name = "isSafeSenterUI";paramCount = 1
                    }.get(instance).invoke<Boolean>(activityName)
                    val appReader = "com.android.server.wm.OplusSplitScreenAppReader".toClass()
                    val getInstance = appReader.method { name = "getInstance" }.get().call()
                    val isInForbidActivityList = getInstance?.current()?.method {
                        name = "isInForbidActivityList";paramCount = 1
                    }?.invoke<Boolean>(activityName)
                    if (isSafeSenterUI == true) resultFalse()
                    else if (candidate && isInForbidActivityList == true) resultFalse()
                    else resultTrue()
                }
            }
        }
    }
}