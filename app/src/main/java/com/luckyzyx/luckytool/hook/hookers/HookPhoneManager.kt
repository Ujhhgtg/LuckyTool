package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.phonemanager.RemoveVirusRiskNotificationInPhoneManager
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
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