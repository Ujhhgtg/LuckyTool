package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookWindowManagerService : YukiBaseHooker() {
    override fun onHook() {
        //移除DPI重启恢复
        val isDpi = prefs(ModulePrefs).getBoolean("remove_dpi_restart_recovery", false)

        //Source OplusWindowManagerService
        "com.android.server.wm.OplusWindowManagerService".toClass().apply {
            method {
                name = "clearForcedDisplayDensityForUser"
                paramCount = 2
                superClass()
            }.hook {
                before {
//                    val i = args().first().int()
//                    val i2 = args().last().int()
//                    YLog.debug("clearForcedDisplayDensityForUser ($i,$i2)")
                    if (isDpi) resultNull()
                }
            }
//            method {
//                name = "setForcedDisplayDensityForUser"
//                paramCount = 3
//                superClass()
//            }.hook {
//                before {
//                    val i = args().first().int()
//                    val i2 = args(1).int()
//                    val i3 = args().last().int()
//                    YLog.debug("setForcedDisplayDensityForUser ($i,$i2,$i3)")
//                }
//            }
        }

        //Source OplusResolutionSwitchImpl
        "com.android.server.wm.OplusResolutionSwitchImpl".toClass().apply {
            if (hasMethod { name = "resetDensityIfNeed" }) {
                method { name = "resetDensityIfNeed" }.hook {
                    if (isDpi) intercept()
                }
            } else {
                method { name = "onResolutionSettingsChange";paramCount = 1 }.hook {
                    before {
                        if (isDpi) args().first().setFalse()
                    }
                }
                if (hasMethod { name = "onFakeResolutionSettingsChange";paramCount = 1 }) {
                    method { name = "onFakeResolutionSettingsChange";paramCount = 1 }.hook {
                        before {
                            if (isDpi) args().first().setFalse()
                        }
                    }
                }
            }
        }
    }
}