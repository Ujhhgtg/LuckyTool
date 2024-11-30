package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveNotificationCleanupButton : YukiBaseHooker() {
    override fun onHook() {
        //Source ClearAllController
        VariousClass(
            "com.oplusos.systemui.notification.ClearAllController", //C12 C13
            "com.oplus.systemui.statusbar.notification.ClearAllController" //C14 C15
        ).toClass().apply {
            method { name = "setVisible";paramCount = 3 }.hook {
                before {
                    args(1).setFalse()
                    args().last().setFalse()
                }
            }
        }
    }
}