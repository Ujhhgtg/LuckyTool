package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object RemoveAccessDeviceLogDialog : YukiBaseHooker() {

    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_access_device_log_dialog", false)

        //Source LogcatManagerService
        "com.android.server.logcat.LogcatManagerService".toClass().apply {
            method { name = "processNewLogAccessRequest" }.hook {
                before {
                    if (!isEnable) return@before
                    val client = args().first().any() ?: return@before
                    method { name = "onAccessApprovedForClient" }.get(instance).call(client)
                    resultNull()
                }
            }
        }
    }
}