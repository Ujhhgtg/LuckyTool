package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookNotificationManager : YukiBaseHooker() {
    override fun onHook() {
        //Source_ext oplus-wifi-service OplusTetheringNotification showSoftapEnabledDurationNotification
        //Channel DurationNotification -> Notification id -> 4
        val hotspotPowerConsumption =
            prefs(ModulePrefs).getBoolean("remove_hotspot_power_consumption_notification", false)

        //Source NotificationManager
        "android.app.NotificationManager".toClass().apply {
            method { name = "notify";paramCount = 3 }.hook {
                before {
                    when (args(1).int()) {
                        4 -> if (hotspotPowerConsumption) resultNull()
                    }
                }
            }
        }

//        //Source OplusTetheringNotification
//        "com.oplus.server.wifi.hotspot.OplusTetheringNotification".toClass().apply {
//            method { name = "showSoftapEnabledDurationNotification" }.hook {
//                intercept()
//            }
//        }

        //notify Soft ap Enabled Time enter, threshold is 1H
        //<string name="wifi_ap_overwork_tips_content">长时间开启个人热点会增加耗电与发热，建议关闭。</string>
        //<string name="tethering_wifi_ap_overwork_tips_content">长时间开启共享 WLAN 会增加耗电与发热，建议关闭。</string>

    }
}
