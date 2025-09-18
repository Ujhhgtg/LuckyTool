package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.permissioncontroller.RemoveStoragePermissionExceptionDialog
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookPermissionController : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->

            val storageDialog =
                prefs(ModulePrefs).getBoolean("remove_storage_permission_exception_dialog", false)
            if (storageDialog) {
                loadHooker(RemoveStoragePermissionExceptionDialog(dexKitBridge))
            }

        }
    }
}