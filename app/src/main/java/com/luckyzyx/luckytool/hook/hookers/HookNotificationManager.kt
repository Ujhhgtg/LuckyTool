package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.notificationmanager.ForceDisplayClockStyleOptionsV14
import com.luckyzyx.luckytool.hook.scopes.notificationmanager.RemoveNotificationManagerLimit
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

@Obfuscate
object HookNotificationManager : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        //移除通知管理限制
        if (prefs(ModulePrefs).getBoolean("remove_notification_manager_limit", false)) {
            loadHooker(RemoveNotificationManagerLimit)
        }
        //强制显示时钟样式选项
        if (prefs(ModulePrefs).getBoolean("force_display_clock_style_options", false)) {
            if (SDK == A14) loadHooker(ForceDisplayClockStyleOptionsV14)
        }

        //Source AppNotificationTopController
        "com.oplus.notificationmanager.property.uicontroller.AppNotificationTopController".toClass()
            .apply {
                method {
                    param(this@apply, "androidx.preference.Preference", AnyClass)
                    returnType = BooleanType
                }.hookAll {
                    before {
                        val bool = args().last().boolean()
                        method { name = "onChange";superClass() }.get(instance).call(bool)
                        resultTrue()
                    }
                }
            }
    }
}