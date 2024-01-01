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

            //Source Phone 启用5G
            if (prefs(ModulePrefs).getBoolean("force_display_five_g_switch", false)) {
                put("ro.oplus.radio.hide_nr_switch", -1)
            }

            //Source SoundRecorder / AtlasService 三方应用通话录音
            if (prefs(ModulePrefs).getBoolean("enable_record_calls_on_third_party_apps", false)) {
                put("ro.oplus.audio.voip_record_white_app_support", true)
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