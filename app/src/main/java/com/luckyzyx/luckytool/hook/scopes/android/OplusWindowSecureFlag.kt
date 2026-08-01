package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs

object OplusWindowSecureFlag : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_screenshot_privacy_limit", false)
        if (!isEnable) return

        //Source OplusLongshotMainWindow
        "com.android.server.wm.OplusLongshotMainWindow".toClass().resolve().apply {
            firstMethod { name = "hasSecure" }.hook {
                replaceToFalse()
            }
        }
    }
}