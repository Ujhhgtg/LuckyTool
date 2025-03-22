package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.intentresolver.HideAppShareIntentList
import com.luckyzyx.luckytool.utils.IntentPrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookIntentResolver : YukiBaseHooker() {
    override fun onHook() {
        //隐藏App分享意图列表
        if (prefs(IntentPrefs).getBoolean("enable_share_intent_switch", false)) {
            loadHooker(HideAppShareIntentList)
        }
    }
}