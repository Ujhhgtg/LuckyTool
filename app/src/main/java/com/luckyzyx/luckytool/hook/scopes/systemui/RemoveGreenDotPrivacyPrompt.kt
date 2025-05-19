package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveGreenDotPrivacyPrompt : YukiBaseHooker() {
    override fun onHook() {
        //Source ViewState
        VariousClass(
            "com.oplusos.systemui.statusbar.events.ViewState", //C13
            "com.oplus.systemui.privacy.ViewState" //C14 C15
        ).toClass().apply {
            method { name = "shouldShowDot" }.hook {
                replaceToFalse()
            }
        }

        //Source ViewState
        "com.android.systemui.statusbar.events.ViewState".toClass().apply {
            method { name = "shouldShowDot" }.hook {
                replaceToFalse()
            }
        }
    }
}