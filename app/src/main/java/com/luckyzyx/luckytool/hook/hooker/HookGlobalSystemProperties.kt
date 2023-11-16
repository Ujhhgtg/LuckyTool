package com.luckyzyx.luckytool.hook.hooker

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.android.HookSystemProperties

object HookGlobalSystemProperties : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            //Source Settings 启用全息音频
//            if (prefs(ModulePrefs).getBoolean("enable_holographic_audio", false)) {
//                put("ro.oplus.audio.support.meta_audio", 1)
//            }
        }
        loadHooker(HookSystemProperties(list))
    }
}