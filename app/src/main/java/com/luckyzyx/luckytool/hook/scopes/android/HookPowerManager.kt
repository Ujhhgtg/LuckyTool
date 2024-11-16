package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookPowerManager : YukiBaseHooker() {
    override fun onHook() {
        val removeThermal =
            prefs(ModulePrefs).getBoolean("disable_temperature_control_listener", false)
        if (!removeThermal) return

        //Source PowerManager
        "android.os.PowerManager".toClass().apply {
            method { name = "addThermalStatusListener";paramCount = 1 }.hook {
                after {
                    val listener = args().first().any() ?: return@after
                    method { name = "removeThermalStatusListener";paramCount = 1 }.get(instance)
                        .call(listener)
                }
            }
            method { name = "getCurrentThermalStatus" }.hook {
                replaceTo(0)
            }
        }
    }
}