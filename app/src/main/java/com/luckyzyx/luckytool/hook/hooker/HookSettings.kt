package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.settings.AllowDisablingSystemApps
import com.luckyzyx.luckytool.hook.scope.settings.AutoUnlockRestrictedSettings
import com.luckyzyx.luckytool.hook.scope.settings.CustomizeDeviceSharingPageParameters
import com.luckyzyx.luckytool.hook.scope.settings.DarkModeList
import com.luckyzyx.luckytool.hook.scope.settings.EnableCustomAppLanguage
import com.luckyzyx.luckytool.hook.scope.settings.EnableStatusBarClockFormat
import com.luckyzyx.luckytool.hook.scope.settings.ForceDisplayBottomGoogleSettings
import com.luckyzyx.luckytool.hook.scope.settings.ForceDisplayContentRecommend
import com.luckyzyx.luckytool.hook.scope.settings.ForceDisplayDCBackLightMode
import com.luckyzyx.luckytool.hook.scope.settings.ForceDisplayDisabledAppsManager
import com.luckyzyx.luckytool.hook.scope.settings.ForceDisplayProcessManagement
import com.luckyzyx.luckytool.hook.scope.settings.HookAppDetails
import com.luckyzyx.luckytool.hook.scope.settings.HookIris5Controller
import com.luckyzyx.luckytool.hook.scope.settings.HookSettingsFeature
import com.luckyzyx.luckytool.hook.scope.settings.RemoveDpiRestartRecovery
import com.luckyzyx.luckytool.hook.scope.settings.RemoveSettingsBottomLaboratory
import com.luckyzyx.luckytool.hook.scope.settings.RemoveTopAccountDisplay
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK


object HookSettings : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)
        loadHooker(HookGlobalSystemProperties)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //HookSettingsFeature
            loadHooker(HookSettingsFeature(dexKitBridge))
            //暗色模式列表
            if (prefs(ModulePrefs).getBoolean("dark_mode_list_enable", false)) {
                loadHooker(DarkModeList)
            }
            //自动解锁受限制的设置
            if (prefs(ModulePrefs).getBoolean("auto_unlock_restricted_settings", false)) {
                if (SDK >= A13) loadHooker(AutoUnlockRestrictedSettings)
            }
        }

        //应用详情页
        loadHooker(HookAppDetails)

        //移除顶部账号显示
        if (prefs(ModulePrefs).getBoolean("remove_top_account_display", false)) {
            loadHooker(RemoveTopAccountDisplay)
        }
        //视频动态插帧
        if (prefs(ModulePrefs).getBoolean("video_frame_insertion_support_2K120", false)) {
            loadHooker(HookIris5Controller)
        }
        //移除DPI重启恢复
        if (prefs(ModulePrefs).getBoolean("remove_dpi_restart_recovery", false)) {
            loadHooker(RemoveDpiRestartRecovery)
        }
        //强制显示低亮无频闪护眼
        if (prefs(ModulePrefs).getBoolean("force_display_dc_backlight_mode", false)) {
            loadHooker(ForceDisplayDCBackLightMode)
        }
        //强制显示设置底部Google
        if (prefs(ModulePrefs).getBoolean("force_display_bottom_google_settings", false)) {
            loadHooker(ForceDisplayBottomGoogleSettings)
        }
        //移除设置底部实验室
        if (prefs(ModulePrefs).getBoolean("remove_settings_bottom_laboratory", false)) {
            loadHooker(RemoveSettingsBottomLaboratory)
        }
        //启用状态栏时钟格式
        if (prefs(ModulePrefs).getBoolean("enable_statusbar_clock_format", false)) {
            loadHooker(EnableStatusBarClockFormat)
        }
        //自定义设备分享页面参数
        if (prefs(ModulePrefs).getBoolean("customize_device_sharing_page_parameters", false)) {
            if (SDK >= A13) loadHooker(CustomizeDeviceSharingPageParameters)
        }
        //强制开启进程管理
        if (prefs(ModulePrefs).getBoolean("force_display_process_management", false)) {
            loadHooker(ForceDisplayProcessManagement)
        }
        //允许停用系统应用
        if (prefs(ModulePrefs).getBoolean("allow_disabling_system_apps", false)) {
            loadHooker(AllowDisablingSystemApps)
        }
        //强制显示已停用应用管理器
        if (prefs(ModulePrefs).getBoolean("force_display_disabled_apps_manager", false)) {
            loadHooker(ForceDisplayDisabledAppsManager)
        }
        //强制显示内容推荐
        if (prefs(ModulePrefs).getBoolean("force_display_content_recommend", false)) {
            loadHooker(ForceDisplayContentRecommend)
        }
        //启用自定义应用语言
        if (prefs(ModulePrefs).getBoolean("enable_custom_app_language", false)) {
            loadHooker(EnableCustomAppLanguage)
        }

//        //Source MultiAppVolumeAdjustmentSettingsFragment
//        "com.oplus.settings.feature.sound.multiappvolume.MultiAppVolumeAdjustmentSettingsFragment".toClass()
//            .apply {
//                method { name = "init" }.hook {
//                    before {
//                        val context = field { name = "mContext";superClass() }.get(instance)
//                            .cast<Context>() ?: return@before
//                        val mHandePreference = method {
//                            name = "findPreference";param(CharSequenceClass);superClass()
//                        }.get(instance).call("multi_app_volume_header") ?: return@before
//                        val id = context.resources.getIdentifier(
//                            "image_animation_view", "id", this@HookSettings.packageName
//                        )
//                        val effectiveAnimationView = mHandePreference.current().method {
//                            name = "findViewById"
//                        }.invoke<View>(id) ?: return@before
//                        if (effectiveAnimationView.parent != null) (effectiveAnimationView.parent as ViewGroup).removeView(
//                            effectiveAnimationView
//                        )
//                    }
//                }
//            }

        //<string name="single_pulse_EM_mode_title">单脉冲调光模式</string>
        //prefs key single_pulse_EM_mode_switch
        //com.oplus.settings.feature.othersettings.development.OplusSinglePulseEMDevelopController
        //Settings Secure display_single_pulse_settings_switch Int

        //darkmode_third_app
        //com.oplus.settings.feature.display.darkmode.ui.DarkModeSettingsFragment

        //LTPO
        //persist.oplus.display.vrr
        //persist.oplus.display.vrr.adfr

        //指纹快速解锁 盲解
//        findClass("com.oplus.settings.feature.fingerprint.controller.FingerprintFastUnlockPreferenceController").hook {
//            injectMember {
//                method {
//                    name = "getAvailabilityStatus"
//                }
//                if (false) replaceTo(0)
//            }
//        }

        //keep_screen_on -> 充电时屏幕不休眠
        //settings put global stay_on_while_plugged_in 7
        //com.android.settings.development.StayAwakePreferenceController

        //<string name="airplane_mode">飞行模式</string>
        //com.oplus.settings.feature.network.AirplaneController -> setAirplaneModeOn

        //safecenter_prohibit_monitor safecenter_prohibit_monitor_title -> 禁用权限监控
        //com.oplus.settings.feature.othersettings.development.ProhibitMonitorPreferenceController
    }
}