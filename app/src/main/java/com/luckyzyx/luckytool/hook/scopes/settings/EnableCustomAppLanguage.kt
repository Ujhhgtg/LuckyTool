package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object EnableCustomAppLanguage : YukiBaseHooker() {
    override fun onHook() {
        //Source AppLocaleUtil
        "com.android.settings.applications.AppLocaleUtil".toClass().resolve().apply {
            firstMethod { name = "canDisplayLocaleUi" }.hook {
                replaceToTrue()
            }
        }
        //Source AppLocalePreferenceController
        "com.android.settings.applications.appinfo.AppLocalePreferenceController".toClass()
            .resolve().apply {
            firstMethod { name = "getAvailabilityStatus" }.hook {
                replaceTo(0)
            }
        }
    }
}