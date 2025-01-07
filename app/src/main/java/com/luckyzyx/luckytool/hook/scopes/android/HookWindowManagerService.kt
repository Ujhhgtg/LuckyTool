package com.luckyzyx.luckytool.hook.scopes.android

import android.content.Context
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

@Obfuscate
object HookWindowManagerService : YukiBaseHooker() {
    override fun onHook() {
        //移除DPI重启恢复
        var isDpi = prefs(ModulePrefs).getBoolean("remove_dpi_restart_recovery", false)
        dataChannel.wait<Boolean>("remove_dpi_restart_recovery") { isDpi = it }

        val windowManagerService = "com.android.server.wm.WindowManagerService"

        //Source OplusWindowManagerService
        "com.android.server.wm.OplusWindowManagerService".toClass().apply {
            method {
                name = "clearForcedDisplayDensityForUser"
                paramCount = 2
                superClass()
            }.hook {
                before {
//                    val displayId = args().first().int()
//                    val userId = args().last().int()
//                    YLog.debug("clearForcedDisplayDensityForUser ($displayId | $userId)")
                    if (isDpi) resultNull()
                }
            }
        }

        //Source DisplayWindowSettings
        "com.android.server.wm.DisplayWindowSettings".toClass().apply {
            method { name = "setForcedDensity";paramCount(2..3) }.hookAll {
                before {
                    if (!isDpi) return@before
                    val density = args(1).int()
//                    val userId = if (method.parameterCount == 3) args().last().int() else null
//                    YLog.debug("${method.name} is call -> $density | $userId")

                    val service = field { type = windowManagerService }.get(instance).any()
                        ?: return@before
                    val context = service.current().field { type = ContextClass }.cast<Context>()
                        ?: return@before
                    val resolver = context.contentResolver
                    val forcedDensity = Settings.Secure.getString(
                        resolver, "display_density_forced"
                    )?.toIntOrNull() ?: return@before
                    if (density == 0) args(1).set(forcedDensity)
                }
            }
        }

        //Source DisplayContentExtImpl
        if (SDK >= A14) {
            "com.android.server.wm.DisplayContentExtImpl".toClass().apply {
                method { name = "setForcedDisplayInfoForWmSize";paramCount = 5 }.hook {
                    before {
                        if (!isDpi) return@before
//                    val width = args().first().int()
//                    val height = args(1).int()
//                    val density = args(2).int()
//                    val userId = args(3).int()
                        val service = args().last().any() ?: return@before
//                    YLog.debug("${method.name} is call -> $width | $height | $density | $userId")

                        val context =
                            service.current().field { type = ContextClass }.cast<Context>()
                                ?: return@before
                        val resolver = context.contentResolver
                        val forcedDensity = Settings.Secure.getString(
                            resolver, "display_density_forced"
                        )?.toIntOrNull() ?: return@before
                        args(2).set(forcedDensity)
                    }
                }
            }
        }

        //Source OplusResolutionSwitchImpl
        "com.android.server.wm.OplusResolutionSwitchImpl".toClass().apply {
            if (hasMethod { name = "resetDensityIfNeed" }) {
                method { name = "resetDensityIfNeed" }.hook {
                    before {
                        if (isDpi) resultNull()
                    }
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