package com.luckyzyx.luckytool.hook.scopes.health

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveHealthRootCheck : YukiBaseHooker() {
    override fun onHook() {
        //Source SafetyCheckManager
        "com.heytap.health.safety.safetycheck.SafetyCheckManager".toClass().apply {
            method { param(ActivityClass) }.hook {
                intercept()
            }
        }
    }
}