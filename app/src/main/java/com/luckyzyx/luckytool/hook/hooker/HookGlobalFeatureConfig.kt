package com.luckyzyx.luckytool.hook.hooker

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.android.HookFeatureConfigManager
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookGlobalFeatureConfig : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            if (prefs(ModulePrefs).getBoolean("enable_super_volume_mode", false)) {
                put("oplus.software.audio.super_volume", true)
                put("oplus.software.audio.super_volume_3x", true)
            }
            if (prefs(ModulePrefs).getBoolean("enable_super_volume_mode_for_calls", false)) {
                put("oplus.software.audio.super_volume_call_earpiece", true)
                put("oplus.software.audio.super_volume_call_earpiece_disable", false)
            }
        }
        loadHooker(HookFeatureConfigManager(list))
    }
}