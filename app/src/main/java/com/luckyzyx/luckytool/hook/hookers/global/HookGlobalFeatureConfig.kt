package com.luckyzyx.luckytool.hook.hookers.global

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.android.HookFeatureConfigManager
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode

object HookGlobalFeatureConfig : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        val list = ArrayMap<String, Boolean>().apply {
            //Source SystemUI 强制启用高斯模糊
            if (prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)) {
                put("oplus.software.display.osie_aisdr2hdr_support", false) //C12
            }

            //Source SystemUI 启用超级音量模式
            if (SDK >= A13 &&
                prefs(ModulePrefs).getBoolean("enable_super_volume_mode", false)
            ) {
                put("oplus.software.audio.super_volume", true)
                put("oplus.software.audio.super_volume_3x", true)
            }
            //Source SystemUI 启用通话超级音量模式
            if (osCode >= 27 &&
                prefs(ModulePrefs).getBoolean("enable_super_volume_mode_for_calls", false)
            ) {
                put("oplus.software.audio.super_volume_call_earpiece", true)
                put("oplus.software.audio.super_volume_call_earpiece_disable", false)
            }
            //Source SystemUI FlavorOneFeatureOption 启用应用专属媒体音量
            if (osCode >= 27 &&
                prefs(ModulePrefs).getBoolean("enable_app_specific_media_volume", false)
            ) {
                put("oplus.software.multi_app.volume.adjust.support", true)
            }
            //Source SystemUI / Launcher OplusShellStartingWindowUtils 全应用遮罩
            if (SDK >= A13 && prefs(ModulePrefs).getBoolean("disable_preload_splash", false)) {
                put("oplus.software.wms.disable_preload_splash", true)
            }

            //Source Settings 启用RGB色温球 ColorModeFragment
            if (prefs(ModulePrefs).getBoolean("enable_screen_color_temperature_rgb_palette", false)
            ) {
                put("oplus.software.display.rgb_ball_support", true)
            }
            //Source Settings 启用游戏专属内存 GameBounceUtils
            if (prefs(ModulePrefs).getBoolean("enable_dedicated_ram_for_games", false)) {
                put("oplus.software.game_bounce_support", true)
            }
            //Source Settings ScreenResolutionFragment isResolutionAutoDisableSupport 启用智能切换屏幕分辨率
            if (prefs(ModulePrefs).getBoolean("enable_smart_switching_screen_resolutions", false)) {
                put("oplus.software.display.resolution_switch_disableauto_support", false)
            }

            //Source Settings Iris5SettingsFragment iris5_motion_fluency_optimization_switch 启用视频动态插帧
            if (prefs(ModulePrefs).getBoolean("enable_video_memc_frame_insertion", false)
            ) {
                put("oplus.software.display.pixelworks_enable", true)
                put("oplus.software.display.iris_enable", true)
                put("oplus.software.display.memc_enable", true)
                put("oplus.software.display.game.memc_enable", true)
            }

            //Source Mms 移除验证码悬浮窗 FeatureOption.java / com.oplus.common -> C12
            if (prefs(ModulePrefs).getBoolean("remove_verification_code_floating_window", false)) {
                put("oplus.software.inputmethod.verify_code_enable", false)
            }

            //Source Gestures 启用隔空手势
            if (prefs(ModulePrefs).getBoolean("force_enable_aon_gestures", false)) {
                put("oplus.software.aon_enable", true)
                put("oplus.software.aon_gestureui_enable", true)
            }
            //Source Gestures 启用音量键控制手电筒手势
            if (prefs(ModulePrefs).getBoolean("enable_volume_key_control_flashlight", false)) {
                put("oplus.software.powerkey_disbale_turnoff_torch", false)
                put("oplus.software.key_quickoperate_torch", true)
            }

            //Source PermissionController 解锁默认桌面限制
            if (prefs(ModulePrefs).getBoolean("unlock_default_desktop_limit", false)) {
                put("oplus.software.defaultapp.remove_force_launcher", true)
            }

            //Source MultiApp AppFeatureUtil isRlmPhone
            when (prefs(ModulePrefs).getString("set_multi_app_support_mode", "0")) {
                "1" -> put("oplus.software.multiapp_support_rlm", false)
                "2" -> put("oplus.software.multiapp_support_rlm", true)
            }

            //Source PhoneManager VoiceCallNCVisibilityProvider
            if (SDK >= A14 && prefs(ModulePrefs).getBoolean("enable_clear_voice", false)) {
                put("oplus.hardware.audio.voice_isolation_support", true)
            }

            //Source SystemUI StatusBarFeatureOption isSupportPrivacyCallMode 通话隐私保护
            //Source Phone OplusFeatureOption 通话隐私保护
            if (SDK >= A14 && prefs(ModulePrefs).getBoolean("enable_sound_sealed_call", false)) {
                put("oplus.hardware.audio.dipole_speaker_support", true)
            }

            //Source SmartSidebar EdgePanelFeatureOption 强制启用浮标自动隐藏
            if (SDK == A12 &&
                prefs(ModulePrefs).getBoolean("force_enable_buoy_automatically_hides", false)
            ) {
                put("oplus.software.smart_sidebar", true)
            }

            //Source NotificationManager FeatureOption isSupportsStealthMode VIP模式
            //Source SafeCenter SettingsInsertProvider VIP模式
//            if (SDK >= A14 && prefs(ModulePrefs).getBoolean("enable_vip_mode", false)) {
//                put("oplus.software.stealth_security_mode", true)
//            }
        }
        loadHooker(HookFeatureConfigManager(list))
    }
}