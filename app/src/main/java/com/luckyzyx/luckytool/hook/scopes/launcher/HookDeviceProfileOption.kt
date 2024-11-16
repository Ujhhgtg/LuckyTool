package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookDeviceProfileOption : YukiBaseHooker() {
    override fun onHook() {
        val enableFolder = prefs(ModulePrefs).getBoolean("enable_folder_layout_adjustment", false)
        val folderColumn = prefs(ModulePrefs).getInt("set_icon_columns_in_folder", 3)
        val syncPreview =
            prefs(ModulePrefs).getBoolean("sync_folder_icon_column_number_preview", false)

        val enableDrawer = prefs(ModulePrefs).getBoolean("enable_drawer_layout_adjustment", false)
        val drawerColumn = prefs(ModulePrefs).getInt("set_icon_columns_in_drawer", 4)

        //Source InvariantDeviceProfile
        "com.android.launcher3.InvariantDeviceProfile\$GridOption".toClass().apply {
            constructor { paramCount(2..3) }.hook {
                after {
                    if (enableFolder) {
//                        field { name = "numFolderRows" }.get(instance).set(3)
                        field { name = "numFolderColumns" }.get(instance).set(folderColumn)
                        if (syncPreview && folderColumn > 3) field {
                            name = "numFolderPreview"
                        }.get(instance).set(folderColumn)
                    }
                    if (enableDrawer) {
                        field { name = "numAllAppsColumns" }.get(instance).set(drawerColumn)
                    }
                }
            }
        }

        //Source OplusFolderUtil C13+
        "com.android.launcher3.folder.OplusFolderUtil".toClassOrNull()?.apply {
            method { name = "getFolderMaxCol" }.hook {
                after {
                    val isBigFolder = args().first().boolean()
                    if (isBigFolder) result = 3
                }
            }
        }
    }
}