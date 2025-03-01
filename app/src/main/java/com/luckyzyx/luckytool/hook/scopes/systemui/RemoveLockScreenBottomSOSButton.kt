package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveLockScreenBottomSOSButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusEmergencyButtonControllExImpl
        VariousClass(
            "com.oplus.systemui.keyguard.OplusEmergencyButtonControllExImpl", //C13
            "com.oplus.keyguard.OplusEmergencyButtonExImpl" //C14 C15
        ).toClass().apply {
            val hasDisableButton = hasMethod { name = "disableShowEmergencyButton" }
            val hasUpdateButton = hasMethod { name = "shouldUpdateEmergencyCallButton" }
            if (hasDisableButton) {
                method { name = "disableShowEmergencyButton" }.hook {
                    replaceToTrue()
                }
            } else if (hasUpdateButton) method { name = "shouldUpdateEmergencyCallButton" }.hook {
                before {
                    field { name = "mEmergencyButton" }.get(instance).cast<View>()
                        ?.isVisible = false
                    resultTrue()
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