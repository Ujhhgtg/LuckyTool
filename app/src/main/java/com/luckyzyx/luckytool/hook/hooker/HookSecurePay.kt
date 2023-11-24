package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.securepay.RemoveSecurePayFoundVirusDialog
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookSecurePay : YukiBaseHooker() {
    override fun onHook() {
        //移除支付保护发现病毒对话框
        if (prefs(ModulePrefs).getBoolean("remove_secure_pay_found_virus_dialog", false)) {
            loadHooker(RemoveSecurePayFoundVirusDialog)
        }
    }
}