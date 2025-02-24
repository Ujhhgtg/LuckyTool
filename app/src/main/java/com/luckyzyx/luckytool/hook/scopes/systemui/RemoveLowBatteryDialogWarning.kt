package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveLowBatteryDialogWarning : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusPowerNotificationWarnings
        VariousClass(
            "com.oplusos.systemui.notification.power.OplusPowerNotificationWarnings", //C13
            "com.oplus.systemui.statusbar.notification.power.OplusPowerNotificationWarnings" //C14
        ).toClass().apply {
            val hasSavePower = hasMethod { name = "createSavePowerDialog" }
            val hasSuperSavePower = hasMethod { name = "createSuperSavePowerDialog" }
            if (hasSavePower) method { name = "createSavePowerDialog" }.hook {
                intercept()
            }
            if (hasSuperSavePower) method { name = "createSuperSavePowerDialog" }.hook {
                intercept()
            }
            if (!hasSavePower && !hasSuperSavePower) {
                method { name = "showLowBatteryWarning" }.hook {
                    intercept()
                }
            }
        }
    }
}