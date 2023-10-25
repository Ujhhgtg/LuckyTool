package com.luckyzyx.luckytool.hook.scope.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method

object RemoveBatteryTemperatureControl : YukiBaseHooker() {
    override fun onHook() {
        //Source ThermalControllerCenter
        "com.oplus.thermalcontrol.ThermalControllerCenter".toClass().apply {
            method { name = "onStart" }.hook {
                intercept()
            }
            method { name { it.startsWith("send") } }.hookAll {
                intercept()
            }
            method { name { it.startsWith("start") } }.hookAll {
                intercept()
            }
        }
        //Source ThermalControlMonitor
        "com.oplus.thermalcontrol.ThermalControlMonitor".toClass().apply {
            method { name = "startMonitor" }.hook {
                intercept()
            }
            if (hasMethod { name { it.contains("register") } }) method {
                name { it.startsWith("register") }
            }.hookAll { intercept() }
        }
        //Source ThermalControlUtils
        "com.oplus.thermalcontrol.ThermalControlUtils".toClass().apply {
            method { name = "onStart" }.hook {
                intercept()
            }
            method { name { it.startsWith("register") } }.hookAll {
                intercept()
            }
        }
    }
}