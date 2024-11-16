package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceDisplayDisabledAppsManager : YukiBaseHooker() {
    override fun onHook() {
        //Source DisabledAppsPreferenceController
        "com.android.settings.applications.disableapps.DisabledAppsPreferenceController".toClass()
            .apply {
                method { name = "getAvailabilityStatus" }.hook {
                    replaceTo(0)
                }
            }
    }
}