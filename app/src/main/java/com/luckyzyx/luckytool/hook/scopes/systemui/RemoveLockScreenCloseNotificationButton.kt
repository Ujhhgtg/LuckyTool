package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveLockScreenCloseNotificationButton : YukiBaseHooker() {
    override fun onHook() {
        //Source NotificationPanelViewExt
        VariousClass(
            "com.oplusos.systemui.notification.extend.NotificationPanelViewExt",  //C12 C13
            "com.oplus.systemui.notification.extend.OplusNotificationCloseButtonImp"  //C14
        ).toClass().apply {
            method { name = "setNotificationCloseButton" }.hook {
                intercept()
            }
        }
    }
}