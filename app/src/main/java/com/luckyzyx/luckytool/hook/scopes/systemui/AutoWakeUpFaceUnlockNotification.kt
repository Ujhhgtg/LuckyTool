package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.OplusWindowManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoWakeUpFaceUnlockNotification : YukiBaseHooker() {
    override fun onHook() {
        //Source WakeupScreenHelper
        VariousClass(
            "com.oplusos.systemui.notification.helper.WakeupScreenHelper", //C12.1
            "com.oplus.systemui.statusbar.notification.helper.WakeupScreenHelper", //C13
            "com.oplus.systemui.notification.interruption.wakeup.WakeupScreenHelper" //C14 C15
        ).toClass().apply {
            method { name = "powerOnScreen" }.hook {
                after {
                    OplusWindowManager().requestKeyguard("android.policy:POWER")
                }
            }
        }
    }
}