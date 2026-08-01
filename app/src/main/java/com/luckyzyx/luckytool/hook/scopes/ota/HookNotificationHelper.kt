package com.luckyzyx.luckytool.hook.scopes.ota

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class HookNotificationHelper(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val notifySuccess =
            prefs(ModulePrefs).getBoolean("remove_ota_notify_install_success", false)

        //Source NotificationHelper
        dexKitBridge.findClass {
            matcher {
//                addFieldForType(Context::class.java)
//                addFieldForType(Notification.Builder::class.java)
//                addFieldForType(NotificationManager::class.java)
                usingStrings(
                    "NotificationHelper",
                    "ota_notify_new_channel_id",
                    "ota_notify_new_channel_default_id"
                )
            }
        }.apply {
            checkDataList("NotificationHelper")

            if (notifySuccess) findMethod {
                matcher {
                    paramCount(0)
                    usingStrings("notifyInstallSuccess")
                }
            }.apply {
                checkDataList("notifyInstallSuccess")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        emptyParameters()
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}