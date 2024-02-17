package com.luckyzyx.luckytool.hook.scopes.systemui

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object RemoveSmallWindowReplyWhitelist : YukiBaseHooker() {
    override fun onHook() {
        var set =
            prefs(ModulePrefs).getStringSet("set_small_window_reply_blacklist_list", ArraySet())
        dataChannel.wait<Set<String>>("set_small_window_reply_blacklist_list") { set = it }

        //Source BaseNotificationContentInflater
        VariousClass(
            "com.oplusos.systemui.notification.base.BaseNotificationContentInflater", //C13
            "com.oplus.systemui.statusbar.NotificationListenerExtImpl" //C14
        ).toClass().apply {
            method { name = "showSmallWindowReply" }.hook {
                after {
                    if (set.isEmpty()) return@after
                    val packName = args().first().string()
                    result = set.contains(packName).not()
                }
            }
        }
    }
}