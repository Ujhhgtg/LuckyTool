package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.phonemanager.RemoveVirusRiskNotificationInPhoneManager
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookPhoneManager : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //移除手机管家发现病毒风险通知
            val removeVirusKey = "remove_virus_risk_notification_in_phone_manager"
            if (prefs(ModulePrefs).getBoolean(removeVirusKey, false)) {
                loadHooker(RemoveVirusRiskNotificationInPhoneManager(dexKitBridge))
            }
        }
    }
}