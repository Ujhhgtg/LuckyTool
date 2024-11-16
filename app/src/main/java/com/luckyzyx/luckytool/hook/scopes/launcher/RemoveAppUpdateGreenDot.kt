package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object RemoveAppUpdateGreenDot : YukiBaseHooker() {
    override fun onHook() {
        val mode = prefs(ModulePrefs).getString("set_app_update_dot_display_mode", "0")
        if (mode != "2") return

        //Source BubbleTextView
        "com.android.launcher3.BubbleTextView".toClass().apply {
            method { name = "isShouldShowGreenDot" }.hook {
                replaceToFalse()
            }
        }
    }
}