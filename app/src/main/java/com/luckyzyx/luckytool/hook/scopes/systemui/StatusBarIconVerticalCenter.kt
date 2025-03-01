package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object StatusBarIconVerticalCenter : YukiBaseHooker() {
    override fun onHook() {
        //Source PhoneStatusBarViewExImpl updateContentsPadding
        VariousClass(
            "com.oplusos.systemui.ext.BasePhoneStatusBarViewExt",
            "com.oplus.systemui.statusbar.phone.PhoneStatusBarViewExImpl"
        ).toClass().apply {
            method { name = "getHoleTop" }.hook {
                replaceTo(0)
            }
            method { name = "getHoleBottom" }.hook {
                replaceTo(0)
            }
        }
    }
}