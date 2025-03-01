package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookFeatureOption : YukiBaseHooker() {
    override fun onHook() {
        val transferDock = prefs(ModulePrefs).getBoolean("unlock_transfer_dock", false)
        val recentFiles = prefs(ModulePrefs).getBoolean("unlock_recent_files", false)
        val fluidCloud = prefs(ModulePrefs).getBoolean("unlock_fluid_cloud", false)

        //Source EdgePanelFeatureOption
        "com.coloros.edgepanel.utils.EdgePanelFeatureOption".toClass().apply {
            method { name = "loadFeatureOption" }.hook {
                after {
                    if (recentFiles) field { name = "IS_SHIELD_FILE_BAG" }.get().setFalse()
                    if (fluidCloud) field { name = "IS_SHIELD_FLUID_CLOUD" }.get().setFalse()
                    if (transferDock) field { name = "IS_SHIELD_TRANSFER_DOCK" }.get().setFalse()
                }
            }
        }
    }
}