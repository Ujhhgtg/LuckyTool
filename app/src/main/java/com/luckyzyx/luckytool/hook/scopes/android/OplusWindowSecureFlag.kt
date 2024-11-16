package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object OplusWindowSecureFlag : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_screenshot_privacy_limit", false)
        if (!isEnable) return

        //Source OplusLongshotMainWindow
        "com.android.server.wm.OplusLongshotMainWindow".toClass().apply {
            method { name = "hasSecure" }.hook {
                replaceToFalse()
            }
        }
    }
}