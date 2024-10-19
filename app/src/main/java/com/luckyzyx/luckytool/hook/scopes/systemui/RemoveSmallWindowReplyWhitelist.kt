package com.luckyzyx.luckytool.hook.scopes.systemui

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object RemoveSmallWindowReplyWhitelist : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) loadHooker(SmallWindowReplyWhitelist)
        else loadHooker(SmallWindowReplyWhitelistV14)
    }

    object SmallWindowReplyWhitelist : YukiBaseHooker() {
        override fun onHook() {
            //Source HeadsUpToZoomUtils
            "com.android.systemui.util.HeadsUpToZoomUtils".toClass().apply {
                method { name { it.startsWith("isZoom") } }.hook {
                    replaceToTrue()
                }
            }
        }
    }

    object SmallWindowReplyWhitelistV14 : YukiBaseHooker() {
        override fun onHook() {
            var set =
                prefs(ModulePrefs).getStringSet("set_small_window_reply_blacklist_list", ArraySet())
            dataChannel.wait<Set<String>>("set_small_window_reply_blacklist_list") { set = it }

            //Source BaseNotificationContentInflater / NotificationListenerExtImpl
            VariousClass(
                "com.oplusos.systemui.notification.base.BaseNotificationContentInflater", //C13
                "com.oplus.systemui.statusbar.NotificationListenerExtImpl" //C14
            ).toClass().apply {
                method { name = "showSmallWindowReply" }.hook {
                    before {
                        if (set.isEmpty()) return@before
                        val packName = args().first().string()
                        result = set.contains(packName).not()
                    }
                }
            }
        }
    }
}