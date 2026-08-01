package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object ForceDisplayBottomGoogleSettings : YukiBaseHooker() {
    override fun onHook() {
        //Source GooglePreferenceController
        "com.oplus.settings.feature.homepage.controller.GooglePreferenceController".toClass()
            .resolve().apply {
                firstMethod { name = "getAvailabilityStatus" }.hook {
                    replaceTo(0)
                }
            }
    }
}