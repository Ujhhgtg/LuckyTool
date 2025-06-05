package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableGlobalNotificationSimpleBannerMode : YukiBaseHooker() {
    override fun onHook() {
        //Source FullScreenBannerHelper -> simple_banner_switch_state
        VariousClass(
            "com.oplusos.systemui.notification.helper.FullScreenBannerHelper", //C12.1
            "com.oplus.systemui.statusbar.notification.helper.FullScreenBannerHelper", //C13 C14 C15
            "com.oplus.systemui.notification.interruption.fullscreenbanner.FullScreenBannerHelper" //C15.0.1
        ).toClass().apply {
            method {
                name = "isSimpleBannerEnable"
                returnType = BooleanType
            }.hook {
                replaceToTrue()
            }
        }
    }
}