package com.luckyzyx.luckytool.hook.scopes.ota

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookOTADialogHelper(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val autoDownload = prefs(ModulePrefs).getBoolean("remove_ota_auto_download_dialog", false)

        //Source OTADialogHelper
        dexKitBridge.findClass {
            matcher {
                addMethod { paramTypes(ContextClass) }
                usingStrings("OTADialogHelper", "auto_download_network_type")
            }
        }.apply {
            checkDataList("NotificationHelper find clazz")

            if (autoDownload) findMethod {
                matcher {
                    paramTypes(ContextClass)
                    usingStrings("auto_download_network_type")
                }
            }.apply {
                checkDataList("AutoDownloadDialog find method")

                single().className.toClass().apply {
                    method { name = single().methodName;param(ContextClass) }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}