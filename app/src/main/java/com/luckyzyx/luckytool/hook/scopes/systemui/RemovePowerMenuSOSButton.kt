package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemovePowerMenuSOSButton : YukiBaseHooker() {
    override fun onHook() {
        //Source GlobalActionsUtils
        VariousClass(
            "com.oplusos.systemui.controls.GlobalActionsUtils", //C13
            "com.oplusos.systemui.common.util.GlobalActionsUtils" //C14 C15
        ).toClass().apply {
            val hasShowSos = hasMethod { name = "isShowSosButton" }
            if (hasShowSos) method { name = "isShowSosButton" }.hook {
                replaceToFalse()
            }
        }

        //Source OplusShutdownView
        "com.oplus.systemui.shutdown.OplusShutdownView".toClass().apply {
            method { name = "isShowEmergency" }.hook {
                replaceToFalse()
            }
        }
    }
}