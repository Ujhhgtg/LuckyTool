package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.directui.RemoveTouchAppRecommendCard
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookDirectUI : YukiBaseHooker() {
    override fun onHook() {
        //移除应用推广卡片
        if (prefs(ModulePrefs).getBoolean("remove_touch_app_recommend_card", false)) {
            loadHooker(RemoveTouchAppRecommendCard)
        }
    }
}