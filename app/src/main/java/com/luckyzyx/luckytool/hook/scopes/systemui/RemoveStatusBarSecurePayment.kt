package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveStatusBarSecurePayment : YukiBaseHooker() {
    override fun onHook() {
        //Source StatusBarHelper
        VariousClass(
            "com.oplusos.systemui.statusbar.helper.StatusBarHelper", //C12 C13
            "com.oplus.systemui.common.manager.OplusSystemUiManagerExImpl" //C14
        ).toClass().apply {
            method { name = "handlePaymentDetectionMessage" }.hook {
                intercept()
            }
        }
    }
}