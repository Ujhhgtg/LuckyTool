package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.heytapcloud.RemoveNetworkRestriction
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookCloudService : YukiBaseHooker() {
    override fun onHook() {
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //移除网络限制
            if (prefs(ModulePrefs).getBoolean("remove_network_limit", false)) {
                loadHooker(RemoveNetworkRestriction(dexKitBridge))
            }
        }
    }
}