package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.SDK

@Obfuscate
object RemoveGTModeNotification : YukiBaseHooker() {
    override fun onHook() {
        //Source GTUtils
        VariousClass(
            "com.oplusos.systemui.statusbar.util.GTUtils", //C13
            "com.oplus.systemui.statusbar.util.GTUtils" //C14
        ).toClass().apply {
            method {
                name = if (SDK >= A14) "notifyOpenGtMode"
                else "showOpenGtModeNotify"
            }.hook {
                intercept()
            }
        }
    }
}