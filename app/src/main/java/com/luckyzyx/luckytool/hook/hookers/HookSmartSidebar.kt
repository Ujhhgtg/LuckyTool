package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.smartsidebar.ForceEnableBuoyAutomaticallyHides
import com.luckyzyx.luckytool.hook.scopes.smartsidebar.HookFeatureOption
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getAppSet

object HookSmartSidebar : YukiBaseHooker() {
    override fun onHook() {
        val appSet = prefs(ModulePrefs).getAppSet(packageName)

        loadHooker(HookGlobalFeatureConfig)

        val v14 = appSet[1].toIntOrNull()?.let { it >= 14000000 } ?: false

        //HookFeatureOption
        if (v14) loadHooker(HookFeatureOption)

        if (prefs(ModulePrefs).getBoolean("force_enable_buoy_automatically_hides", false)) {
            if (SDK == A12) loadHooker(ForceEnableBuoyAutomaticallyHides)
        }
    }
}