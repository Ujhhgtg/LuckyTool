package com.luckyzyx.luckytool.hook.hooker

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.android.HookSystemProperties
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookGlobalSystemProperties : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            //Source Settings SysFeatureUtils isHoloAudioSupported 启用全息音频
            if (prefs(ModulePrefs).getBoolean("enable_holographic_audio", false)) {
                put("ro.oplus.audio.support.meta_audio", 1)
            }
            //Source Android OplusFeatureMEMC 启用视频动态插帧
//            if (prefs(ModulePrefs).getBoolean("force_display_video_memc_frame_insertion", false)) {
//                put("ro.oplus.display.memc_video_refreshrate", true)
//                put("vendor.display.show_memc_tomast", true)
//            }
        }
        loadHooker(HookSystemProperties(list))
    }
}