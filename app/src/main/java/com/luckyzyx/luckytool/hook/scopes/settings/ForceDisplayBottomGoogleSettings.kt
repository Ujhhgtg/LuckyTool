package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceDisplayBottomGoogleSettings : YukiBaseHooker() {
    override fun onHook() {
        //Source GooglePreferenceController
        "com.oplus.settings.feature.homepage.controller.GooglePreferenceController".toClass()
            .apply {
                method { name = "getAvailabilityStatus" }.hook {
                    replaceTo(0)
                }
            }
    }
}