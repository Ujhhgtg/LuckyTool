package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object DisableSysUIOTGAutoOff : YukiBaseHooker() {
    override fun onHook() {
        // Search OtgHelper 600000
        VariousClass(
            "com.oplusos.systemui.notification.helper.OtgHelper", //C13
            "com.oplus.systemui.qs.helper.OtgHelper" //C14
        ).toClass().apply {
            method { name = "setAutoCloseAlarm" }.hook {
                intercept()
            }
        }
    }
}