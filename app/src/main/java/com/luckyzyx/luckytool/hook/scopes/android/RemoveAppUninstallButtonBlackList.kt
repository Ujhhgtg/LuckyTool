package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object RemoveAppUninstallButtonBlackList : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_app_uninstall_button_blacklist", false)

        //Source OplusUninstallableConfigManager
        "com.android.server.pm.OplusUninstallableConfigManager".toClass().apply {
            method { name = "loadUninstallableConfig" }.hook {
                after {
                    if (!isEnable) return@after
                    val icon = field { name = "mHideUninstallIcon" }.get(instance).any()
                    icon?.current()?.field { name = "mList" }?.cast<ArraySet<String>>()?.clear()
                    val iconSoft = field { name = "mHideUninstallIconSoft" }.get(instance).any()
                    iconSoft?.current()?.field { name = "mList" }?.cast<ArraySet<String>>()?.clear()
                }
            }
        }
    }
}