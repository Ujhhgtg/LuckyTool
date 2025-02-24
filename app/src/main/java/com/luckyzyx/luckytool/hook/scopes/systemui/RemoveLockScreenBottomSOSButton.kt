package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveLockScreenBottomSOSButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusEmergencyButtonControllExImpl
        VariousClass(
            "com.oplus.systemui.keyguard.OplusEmergencyButtonControllExImpl", //C13
            "com.oplus.keyguard.OplusEmergencyButtonExImpl" //C14 C15
        ).toClass().apply {
            val hasDisableButton = hasMethod { name = "disableShowEmergencyButton" }
            if (hasDisableButton) {
                method { name = "disableShowEmergencyButton" }.hook {
                    replaceToTrue()
                }
            } else {
                method { name = "updateEmergencyCallButton" }.hook {
                    before {
                        args().last().setFalse()
                    }
                }
            }
        }
    }
}