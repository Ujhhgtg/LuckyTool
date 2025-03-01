package com.luckyzyx.luckytool.hook.scopes.cloudservice

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object DisableForcedBackupAppList : YukiBaseHooker() {
    override fun onHook() {
        val backupRestoreOptUiStyle =
            "com.heytap.cloud.backuprestore.bswitch.BackupRestoreOptUiStyle"

        val uiStyleEnum = backupRestoreOptUiStyle.toClass()
        if (!uiStyleEnum.isEnum) {
            YLog.debug("DisableForcedBackupAppList enum is error!")
            return
        }
        val switchStyle = uiStyleEnum.enumConstants.find { it.toString() == "STYLE_SWITCH" }
            ?: return

        //Source BackupRestoreOpt
        "com.heytap.cloud.backuprestore.bswitch.BackupRestoreOpt".toClass().apply {
            method { name = "getForceSelect" }.hook {
                replaceToFalse()
            }
        }

        //Source BackupRestoreOptUiData
        "com.heytap.cloud.backuprestore.bswitch.bean.BackupRestoreOptUiData".toClass().apply {
            method { name = "getOptStyle" }.hook {
                before {
                    val optId = field { name = "optId" }.get(instance).string()
                    if (optId == "backup_switch_key_third_app") {
                        val style = switchStyle.current().method { name = "getStyle" }.int()
                        result = style
                    }
                }
            }
        }
    }
}