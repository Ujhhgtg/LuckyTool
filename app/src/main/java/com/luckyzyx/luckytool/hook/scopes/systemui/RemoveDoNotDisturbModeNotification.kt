package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveDoNotDisturbModeNotification : YukiBaseHooker() {
    override fun onHook() {
        //Source DndAlertHelper
        VariousClass(
            "com.oplusos.systemui.notification.helper.DndAlertHelper",
            "com.coloros.systemui.notification.helper.DndAlertHelper",
            "com.oplus.systemui.statusbar.notification.helper.DndAlertHelper" //C14
        ).toClass().apply {
            method {
                name = "operateNotification"
                name = "operateNotification"
                paramCount = 3
            }.hook {
                intercept()
            }
        }
    }
}