package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveStatusBarTopNotification : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_statusbar_top_notification", false)

        //Source AlertWindowNotification
        "com.android.server.wm.AlertWindowNotification".toClass().resolve().optional().apply {
            firstMethod { name = "onPostNotification" }.hook {
                if (isEnable) intercept()
            }
        }
    }
}