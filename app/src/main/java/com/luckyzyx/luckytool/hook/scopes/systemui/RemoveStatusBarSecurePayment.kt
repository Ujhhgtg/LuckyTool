package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.MessageClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveStatusBarSecurePayment : YukiBaseHooker() {
    override fun onHook() {
        //Source SecurePaymentController
        VariousClass(
            "com.oplus.systemui.statusbar.phone.securepay.SecurePaymentControllerExImpl", //C12 C13
            "com.oplus.systemui.statusbar.phone.dynamic.SecurePaymentController" //C14
        ).toClass().apply {
            method {
                name = "handlePaymentDetectionMessage"
                param(MessageClass)
            }.hook {
                intercept()
            }
        }
    }
}