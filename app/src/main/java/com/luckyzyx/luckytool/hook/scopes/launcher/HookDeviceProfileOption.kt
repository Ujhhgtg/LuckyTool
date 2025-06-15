package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookDeviceProfileOption : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        val enableFolder = prefs(ModulePrefs).getBoolean("enable_folder_layout_adjustment", false)
        val folderRow = prefs(ModulePrefs).getInt("set_icon_rows_in_folder", 4)
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
                        field { name = "numFolderRows" }.get(instance).set(folderRow)
                        field { name = "numFolderColumns" }.get(instance).set(folderColumn)
                        if (syncPreview && folderColumn > 3) {
                            field { name = "numFolderPreview" }.get(instance).set(folderColumn)
                        }
                    }
                    if (enableDrawer) {
                        field { name = "numAllAppsColumns" }.get(instance).set(drawerColumn)
                    }
                }
            }
        }

        //Source OplusInvariantDeviceProfile
        "com.android.launcher3.OplusInvariantDeviceProfile".toClass().apply {
            method { name = "injectInitGridForCustomAttr" }.hook {
                after {
                    if (enableFolder) {
//                        field { name = "numFolderRows" }.get(instance).set(3)
                        if (hasField { name = "numFolderRows" }) {
                            field { name = "numFolderRows" }.get(instance).set(folderRow)
                        }
                        if (hasField { name = "numFolderColumns" }) {
                            field { name = "numFolderColumns" }.get(instance).set(folderColumn)
                        }
                        if (syncPreview && folderColumn > 3) {
                            field { name = "numFolderPreview" }.get(instance).set(folderColumn)
                        }
                    }
                    if (enableDrawer) {
                        field { name = "numAllAppsColumns";superClass() }.get(instance)
                            .set(drawerColumn)
                    }
                }
            }
        }

        if (osCode >= 34) return

        //Source FolderInfo
        "com.android.launcher3.model.data.FolderInfo".toClass().apply {
            val hasRow = hasMethod { name = "getPreviewRow" }
            val hasCol = hasMethod { name = "getPreviewColumn" }
            if (hasRow) method { name = "getPreviewRow" }.hook {
                before {
                    if (!(enableFolder && syncPreview)) return@before
                    val spanX = field { name = "spanX";superClass() }.get(instance).int()
                    val spanY = field { name = "spanY";superClass() }.get(instance).int()
                    if (spanX == 1 && spanY == 1) result = folderRow
                }
            }
            if (hasCol) method { name = "getPreviewColumn" }.hook {
                before {
                    if (!(enableFolder && syncPreview)) return@before
                    val spanX = field { name = "spanX";superClass() }.get(instance).int()
                    val spanY = field { name = "spanY";superClass() }.get(instance).int()
                    if (spanX == 1 && spanY == 1) result = folderColumn
                }
            }
        }

        //Source AllAppsParam
        "com.android.launcher.layoutparam.AllAppsParam".toClassOrNull()?.apply {
            method { name = "getNumAllAppsColumns" }.hook {
                before {
                    if (enableDrawer) result = drawerColumn
                }
            }
        }
    }
}