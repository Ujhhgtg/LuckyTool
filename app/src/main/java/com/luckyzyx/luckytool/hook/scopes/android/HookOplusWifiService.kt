package com.luckyzyx.luckytool.hook.scopes.android

import com.android.internal.os.SystemServerClassLoaderFactory
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookOplusWifiService : YukiBaseHooker() {
    override fun onHook() {
        //Source_ext oplus-wifi-service OplusTetheringNotification showSoftapEnabledDurationNotification
        //Channel DurationNotification -> Notification id -> 4
        val hotspotPowerConsumption =
            prefs(ModulePrefs).getBoolean("remove_hotspot_power_consumption_notification", false)
        if (hotspotPowerConsumption.not()) return

        try {
            val wifiserviceClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                "/apex/com.android.wifi/javalib/service-wifi.jar",
                null,
                false
            )
            val finalClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                "/system_ext/framework/oplus-wifi-service.jar",
                wifiserviceClassLoader,
                false
            )
            //Source OplusSoftapStatistics
            "com.oplus.server.wifi.hotspot.OplusSoftapStatistics".toClass(finalClassLoader).apply {
                method { name = "startSoftapEnableTimer" }.hook {
                    intercept()
                }
            }
//            YLog.debug("wifiserviceClassLoader success!")
        } catch (t: Throwable) {
//            YLog.debug("Hook OplusWifiService Error!", t)
            return
        }
    }
}