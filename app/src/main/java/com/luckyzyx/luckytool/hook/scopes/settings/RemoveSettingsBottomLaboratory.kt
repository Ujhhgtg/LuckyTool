package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveSettingsBottomLaboratory : YukiBaseHooker() {
    override fun onHook() {
        //Source TopLevelLaboratoryPreferenceController
        "com.oplus.settings.feature.homepage.TopLevelLaboratoryPreferenceController".toClass()
            .apply {
                method { name = "getAvailabilityStatus" }.hook {
                    replaceTo(3)
                }
            }
    }
}