package com.luckyzyx.luckytool.hook.scopes.keyguardclock

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object LockScreenClockRedMode : YukiBaseHooker() {
    override fun onHook() {
        var redMode = prefs(ModulePrefs).getString("lock_screen_clock_redone_mode", "0")
        dataChannel.wait<String>("lock_screen_clock_redone_mode") { redMode = it }

        //Source CustomizedTextView
        "com.oplus.keyguard.clock.base.widget.CustomizedTextView".toClass().apply {
            method { name = "setHourText" }.hook {
                before {
                    args().first().set(
                        when (redMode) {
                            "1" -> true
                            "2" -> false
                            else -> return@before
                        }
                    )
                }
            }
        }
    }
}