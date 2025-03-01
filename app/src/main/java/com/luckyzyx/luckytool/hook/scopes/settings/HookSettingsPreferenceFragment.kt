package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookSettingsPreferenceFragment : YukiBaseHooker() {
    override fun onHook() {
        //启用应用专属媒体音量
        val specificMediaVolume =
            prefs(ModulePrefs).getBoolean("enable_app_specific_media_volume", false)

        //Source SettingsPreferenceFragment
        "com.android.settings.SettingsPreferenceFragment".toClass().apply {
            method { name = "removePreference";param(StringClass) }.hook {
                before {
                    when (args().first().string()) {
                        "voice_mode_category" -> if (specificMediaVolume) resultTrue()
                    }
                }
            }
        }
    }
}