package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object ForceDisplayDisabledAppsManager : YukiBaseHooker() {
    override fun onHook() {
        //Source DisabledAppsPreferenceController
        "com.android.settings.applications.disableapps.DisabledAppsPreferenceController".toClass()
            .resolve().apply {
                firstMethod { name = "getAvailabilityStatus" }.hook {
                    replaceTo(0)
                }
            }
    }
}