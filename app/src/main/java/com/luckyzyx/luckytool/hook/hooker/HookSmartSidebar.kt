package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.smartsidebar.HookFeatureOption
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppSet

object HookSmartSidebar : YukiBaseHooker() {
    override fun onHook() {
        val appSet = getAppSet(ModulePrefs, packageName)
        appSet[1].toIntOrNull().takeIf { it != null && it >= 14000000 } ?: return
        //HookFeatureOption
        loadHooker(HookFeatureOption)
    }
}