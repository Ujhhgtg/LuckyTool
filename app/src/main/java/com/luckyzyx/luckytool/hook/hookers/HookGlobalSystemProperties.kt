package com.luckyzyx.luckytool.hook.hookers

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.android.HookSystemProperties
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookGlobalSystemProperties : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            //Source SystemUI OplusVolumeDialogImpl 音量条位置
            when (prefs(ModulePrefs).getString("set_volume_bar_display_position", "0")) {
                "1" -> put("persist.oplus.software.audio.right_volume_key", false)
                "2" -> put("persist.oplus.software.audio.right_volume_key", true)
            }


//            //Source SystemUI 音量对话框背景透明度
//            val volumeBlur =
//                prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
//            if (volumeBlur > -1) put("ro.oplus.display.disable.volume_blur", false)
//
//            //Source SystemUI 强制启用高斯模糊
//            if (prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)) {
//                put("ro.surface_flinger.supports_background_blur", true)
//            }

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