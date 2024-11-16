package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookMediaProjectionManager : YukiBaseHooker() {
    override fun onHook() {
        val isEnable =
            prefs(ModulePrefs).getBoolean("enable_record_calls_on_third_party_apps", false)

        //Source MediaProjectionManagerServiceExtImpl
        "android.media.projection.MediaProjectionManagerServiceExtImpl".toClassOrNull()?.apply {
            method { name = "isOplusApp";paramCount = 1 }.hook {
                after {
                    if (!isEnable) return@after
                    val packageName = args().first().string()
                    if (packageName == "com.oplus.audiomonitor") resultTrue()
                }
            }
        }
    }
}