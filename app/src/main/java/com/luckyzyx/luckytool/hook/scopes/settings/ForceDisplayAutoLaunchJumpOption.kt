package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceDisplayAutoLaunchJumpOption : YukiBaseHooker() {
    override fun onHook() {
        //Source AutoLaunchMgrPreferenceController
        "com.oplus.settings.feature.appmanager.controller.AutoLaunchMgrPreferenceController".toClass()
            .apply {
                method { name = "getAvailabilityStatus" }.hook {
                    replaceTo(0)
                }
            }
    }
}