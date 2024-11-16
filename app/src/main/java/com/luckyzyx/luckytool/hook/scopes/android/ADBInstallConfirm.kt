package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object ADBInstallConfirm : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_adb_install_confirm", false)

        //Source OplusPackageInstallInterceptManager
        VariousClass(
            "com.android.server.pm.ColorPackageInstallInterceptManager", //A11
            "com.android.server.pm.OplusPackageInstallInterceptManager"
        ).toClass().apply {
            method { name = "allowInterceptAdbInstallInInstallStage" }.hook {
                if (isEnable) replaceToFalse()
            }
        }
    }
}