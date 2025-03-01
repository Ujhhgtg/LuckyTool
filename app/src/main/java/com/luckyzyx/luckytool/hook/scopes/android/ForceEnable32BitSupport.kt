package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object ForceEnable32BitSupport : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("force_enable_32_bit_support", false)
        if (!isEnable) return

        //Source OplusPackageManagerHelper
        "com.android.server.pm.OplusPackageManagerHelper".toClass().apply {
            method { name = "allowInstall32BitApp" }.hook {
                replaceToTrue()
            }
        }
    }
}