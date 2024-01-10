package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass

object HookSettingsPreferenceFragment : YukiBaseHooker() {
    override fun onHook() {
        //Source SettingsPreferenceFragment
        "com.android.settings.SettingsPreferenceFragment".toClass().apply {
            method { name = "removePreference";param(StringClass) }.hook {
                before {
                    val key = args().first().string()
                    if (key.contains("voice_mode_category")) resultTrue()
                }
            }
        }
    }
}