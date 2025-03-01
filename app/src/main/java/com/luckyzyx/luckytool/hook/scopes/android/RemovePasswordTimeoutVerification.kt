package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object RemovePasswordTimeoutVerification : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_72hour_password_verification", false)

        //Source LockSettingsStrongAuth -> StrongAuthTimeoutAlarmListener
        "com.android.server.locksettings.LockSettingsStrongAuth".toClass().apply {
            method { name = "rescheduleStrongAuthTimeoutAlarm";paramCount = 2 }.hook {
                if (isEnable) intercept()
            }
        }
    }
}