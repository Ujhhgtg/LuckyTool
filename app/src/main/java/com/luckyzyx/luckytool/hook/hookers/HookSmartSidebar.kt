package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.smartsidebar.HookFeatureOption
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppSet

object HookSmartSidebar : YukiBaseHooker() {
    override fun onHook() {
        val appSet = prefs(ModulePrefs).getAppSet(packageName)
        appSet[1].toIntOrNull().takeIf { it != null && it >= 14000000 } ?: return

        //HookFeatureOption
        loadHooker(HookFeatureOption)
    }
}