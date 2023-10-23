package com.luckyzyx.luckytool.hook.scope.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveBatteryTemperatureControl : YukiBaseHooker() {
    override fun onHook() {
        //Source ThermalControlConfig
        "com.oplus.thermalcontrol.ThermalControlConfig".toClass().apply {
            method { name = "isThermalControlEnable" }.hook {
                replaceToFalse()
            }
        }
        //Source ThermalControllerCenter
        "com.oplus.thermalcontrol.ThermalControllerCenter".toClass().apply {
            method { name = "onStart" }.hook {
                after { method { name = "onDestory" }.get(instance).call() }
            }
            method { name { it.startsWith("send") || it.startsWith("start") } }.hookAll {
                intercept()
            }
        }

        //customize_power_temperature_control_videosr
        //customize_power_temperature_control_osie
        //customize_power_temperature_control_hbm
    }
}