package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableKeepNotificationWhenAppStop : YukiBaseHooker() {
    override fun onHook() {
        var isEnable =
            prefs(ModulePrefs).getBoolean("enable_keep_notification_when_app_stop", false)
        dataChannel.wait<Boolean>("enable_keep_notification_when_app_stop") { isEnable = it }

        //Source NotificationManagerService -> cancelAllNotificationsInt
        //Source OplusNotificationManagerServiceExtImpl -> shouldKeepNotifcationWhenForceStop
        //Source OplusNotificationCommonPolicy -> shouldKeepNotifcationWhenForceStop
        "com.android.server.notification.OplusNotificationManagerServiceExtImpl".toClass().apply {
            method { name = "shouldKeepNotifcationWhenForceStop" }.hook {
                before {
                    if (isEnable) resultTrue()
                }
            }
        }
    }
}