package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.notificationmanager.ForceDisplayClockStyleOptionsV14
import com.luckyzyx.luckytool.hook.scope.notificationmanager.RemoveNotificationManagerLimit
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookNotificationManager : YukiBaseHooker() {
    override fun onHook() {
        //移除通知管理限制
        if (prefs(ModulePrefs).getBoolean("remove_notification_manager_limit", false)) {
            loadHooker(RemoveNotificationManagerLimit)
        }
        //强制显示时钟样式选项
        if (prefs(ModulePrefs).getBoolean("force_display_clock_style_options", false)) {
            if (SDK >= A14) loadHooker(ForceDisplayClockStyleOptionsV14)
        }

        //通知智能隐藏
        //com.oplus.notificationmanager.SmartAntiVoyeurActivity
        //com.oplus.notificationmanager.fragments.antivoyeur.SmartAntiVoyeurFragment
//            findClass("com.oplus.notificationmanager.config.FeatureOption").hook {
//                injectMember {
//                    method { name = "loadFeatureOption" }
//                    afterHook {
//                        field { name = "IS_AON_ANT_PEEP_DISABLE" }.get().setFalse()
//                    }
//                }
//                injectMember {
//                    method { name = "loadFeatureServiceOption" }
//                    afterHook {
//                        field { name = "IS_AON_ANT_PEEP_DISABLE" }.get().setFalse()
//                    }
//                }
//                injectMember {
//                    method { name = "isSmartAntiVoyeurEnabled" }
//                    replaceToTrue()
//                }
//            }
    }
}