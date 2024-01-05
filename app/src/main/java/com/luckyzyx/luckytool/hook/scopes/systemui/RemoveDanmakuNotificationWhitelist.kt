package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveDanmakuNotificationWhitelist : YukiBaseHooker() {
    override fun onHook() {
        //Source DanmakuHelper
        VariousClass(
            "com.oplusos.systemui.notification.helper.DanmakuHelper", //C13
//            "com.oplus.systemui.statusbar.notification.helper.HeadsUpHelper" //C14
        ).toClass().apply {
            method {
                name = "isSupportDanmaku"
            }.hook { replaceToTrue() }
        }
    }
}