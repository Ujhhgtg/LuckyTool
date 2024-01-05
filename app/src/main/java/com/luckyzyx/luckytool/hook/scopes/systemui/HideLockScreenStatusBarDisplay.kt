package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object HideLockScreenStatusBarDisplay : YukiBaseHooker() {
    override fun onHook() {
        //Source KeyguardStatusBarView
        VariousClass(
            "com.android.systemui.statusbar.phone.KeyguardStatusBarView",  //C12.1 C13.1
            "com.android.systemui.statusbar.phone.KeyguardStatusBarView"  //C14
        ).toClass().apply {
            method { name = "setVisibility" }.hook {
                before {
                    args().first().set(View.INVISIBLE)
                }
            }
        }
    }
}