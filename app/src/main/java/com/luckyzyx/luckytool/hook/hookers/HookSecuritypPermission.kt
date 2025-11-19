package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.securitypermission.AppStartDialogUseOldVersion
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookSecuritypPermission : YukiBaseHooker() {
    override fun onHook() {
        //使用旧版跳转应用对话框
        if (prefs(ModulePrefs).getBoolean("app_start_dialog_use_old_version", false)) {
            loadHooker(AppStartDialogUseOldVersion)
        }
    }
}