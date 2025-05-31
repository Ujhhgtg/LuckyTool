package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.quicksearchbox.HookQuickSearchBoxMMKV
import com.luckyzyx.luckytool.hook.scopes.quicksearchbox.RemoveSearchBoxAppRecommendCard
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookQuickSearchBox : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            loadHooker(HookQuickSearchBoxMMKV(dexKitBridge))
        }

        //移除应用推广卡片
        if (prefs(ModulePrefs).getBoolean("remove_searchbox_app_recommend_card", false)) {
            loadHooker(RemoveSearchBoxAppRecommendCard)
        }

    }
}