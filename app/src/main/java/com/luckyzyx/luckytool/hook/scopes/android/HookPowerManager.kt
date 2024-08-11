package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookPowerManager : YukiBaseHooker() {
    override fun onHook() {
        val removeThermal =
            prefs(ModulePrefs).getBoolean("disable_temperature_control_listener", false)

        //Source PowerManager
        "android.os.PowerManager".toClass().apply {
            method { name = "addThermalStatusListener" }.hookAll {
                if (removeThermal) intercept()
            }
        }
    }
}