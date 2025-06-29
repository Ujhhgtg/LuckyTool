package com.luckyzyx.luckytool.hook.scopes.ota

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
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
                addMethod { paramTypes(Context::class.java) }
                usingStrings("OTADialogHelper", "auto_download_network_type")
            }
        }.apply {
            checkDataList("NotificationHelper find clazz")

            if (autoDownload) findMethod {
                matcher {
                    paramTypes(Context::class.java)
                    usingStrings("auto_download_network_type")
                }
            }.apply {
                checkDataList("AutoDownloadDialog find method")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        parameters(Context::class)
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}