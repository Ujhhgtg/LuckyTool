package com.luckyzyx.luckytool.hook.scopes.ota

import android.app.NotificationManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.Notification_BuilderClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookNotificationHelper(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val notifySuccess =
            prefs(ModulePrefs).getBoolean("remove_ota_notify_install_success", false)

        //Source NotificationHelper
        dexKitBridge.findClass {
            matcher {
                addFieldForType(ContextClass)
                addFieldForType(Notification_BuilderClass)
                addFieldForType(NotificationManager::class.java)
                usingStrings(
                    "NotificationHelper",
                    "ota_notify_new_channel_id",
                    "ota_notify_new_channel_default_id"
                )
            }
        }.apply {
            checkDataList("NotificationHelper find clazz")

            if (notifySuccess) findMethod {
                matcher {
                    paramCount(0)
                    usingStrings("notifyInstallSuccess")
                }
            }.apply {
                checkDataList("notifyInstallSuccess find method")

                single().className.toClass().apply {
                    method { name = single().methodName;emptyParam() }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}