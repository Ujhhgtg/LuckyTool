package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.OplusWindowManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoWakeUpFaceUnlockNotification : YukiBaseHooker() {
    override fun onHook() {
        //Source WakeupScreenHelper
        "com.oplus.systemui.notification.interruption.wakeup.WakeupScreenHelper".toClass().apply {
            method { name = "powerOnScreen" }.hook {
                after {
                    OplusWindowManager().requestKeyguard("android.policy:POWER")
                }
            }
        }
    }
}