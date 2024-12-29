package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemovePowerMenuSOSButton : YukiBaseHooker() {
    override fun onHook() {
        //Source GlobalActionsUtils
        VariousClass(
            "com.oplusos.systemui.controls.GlobalActionsUtils", //C13
            "com.oplusos.systemui.common.util.GlobalActionsUtils" //C14 C15
        ).toClass().apply {
            method { name = "isShowSosButton" }.hook {
                replaceToFalse()
            }
        }
    }
}