package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object StatusBarIconVerticalCenter : YukiBaseHooker() {
    override fun onHook() {
        //Source PhoneStatusBarViewExImpl updateContentsPadding
        VariousClass(
            "com.oplusos.systemui.ext.BasePhoneStatusBarViewExt",
            "com.oplus.systemui.statusbar.phone.PhoneStatusBarViewExImpl"
        ).toClass().resolve().apply {
            firstMethod { name = "getHoleTop" }.hook {
                replaceTo(0)
            }
            firstMethod { name = "getHoleBottom" }.hook {
                replaceTo(0)
            }
        }
    }
}