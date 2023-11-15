package com.luckyzyx.luckytool.hook.scope.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.hooker.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookSystemUIFeature : YukiBaseHooker() {
    var callback: ((key: String, value: Any) -> Unit)? = null

    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        loadHooker(HookFeatureOption)
        loadHooker(HookStatusBarFeature)
        loadHooker(HookNotificationAppFeature)
        loadHooker(HookFlavorOneFeature)
        if (SDK >= A14) {
            loadHooker(HookQSFeatureOption)
            loadHooker(HookVolumeFeatureOption)
        }
        if (SDK < A13) loadHooker(HookRegionalGaussBlurController)
    }

    private object HookFeatureOption : YukiBaseHooker() {
        override fun onHook() {
            //自动亮度 com.android.systemui.remove_auto_brightness
            val autoBrightnessMode =
                prefs(ModulePrefs).getString("set_auto_brightness_button_mode", "0")
            //全屏充电动画 com.android.systemui.support_fullscreen_charge_anim
            val fullScreenChargeAnim =
                prefs(ModulePrefs).getString("set_full_screen_charging_animation_mode", "0")
            //通知重要性 com.android.systemui.origin_notification_behavior
            val notifyImportance = prefs(ModulePrefs).getBoolean(
                "enable_notification_importance_classification", false
            )
            //高斯模糊
            val enableBlur =
                prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)
            //音量条位置
            val volumePosition =
                prefs(ModulePrefs).getString("set_volume_bar_display_position", "0")
            //音量对话框背景透明度
            var volumeBlur =
                prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
            dataChannel.wait<Int>("custom_volume_dialog_background_transparency") {
                volumeBlur = it
                callback?.invoke("custom_volume_dialog_background_transparency", it)
            }
            //锁屏充电显示瓦数
            var showWattage =
                prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
            dataChannel.wait<Boolean>("force_lock_screen_charging_show_wattage") {
                showWattage = it
                callback?.invoke("force_lock_screen_charging_show_wattage", it)
            }
            //WARP充电器样式
            var warpCharge =
                prefs(ModulePrefs).getString("set_lock_screen_warp_charging_style", "0")
            dataChannel.wait<String>("set_lock_screen_warp_charging_style") { warpCharge = it }
            //移除我的设备
            val removeMyDevice =
                prefs(ModulePrefs).getBoolean("remove_control_center_mydevice", false)
            //强制显示时钟样式选项
            val forceDisplayClockStyle =
                prefs(ModulePrefs).getBoolean("force_display_clock_style_options", false)

            //Source FeatureOption
            "com.oplusos.systemui.common.feature.FeatureOption".toClass().apply {
                //C12 C13
                if (hasMethod { name = "shouldRemoveAutoBrightness" }) {
                    method { name = "shouldRemoveAutoBrightness" }.hook {
                        before {
                            when (autoBrightnessMode) {
                                "1" -> resultFalse()
                                "2" -> resultTrue()
                            }
                        }
                    }
                }
                //C12 C13
                if (hasMethod { name = "isOriginNotificationBehavior" }) {
                    method { name = "isOriginNotificationBehavior" }.hook {
                        if (notifyImportance) replaceToTrue()
                    }
                }
                //C12 C13
                if (hasMethod { name = "isVolumeBlurDisabled" }) {
                    method { name = "isVolumeBlurDisabled" }.hook {
                        if (volumeBlur > -1) replaceToFalse()
                    }
                }
                //C12
                if (hasMethod { name = "isAiSdr2HdrSupport" }) {
                    method { name = "isAiSdr2HdrSupport" }.hook {
                        if (enableBlur) replaceToFalse()
                    }
                }
                //C13 C14
                if (hasMethod { name = "isOplusVolumeKeyInRight" }) {
                    method { name = "isOplusVolumeKeyInRight" }.hook {
                        before {
                            when (volumePosition) {
                                "1" -> resultFalse()
                                "2" -> resultTrue()
                            }
                        }
                    }
                }
                //C13
                if (hasMethod { name = "areVolumeAndPowerKeysInRight" }) {
                    method { name = "areVolumeAndPowerKeysInRight" }.hook {
                        before {
                            when (volumePosition) {
                                "1" -> resultFalse()
                                "2" -> resultTrue()
                            }
                        }
                    }
                }
                //C13
                if (hasMethod { name = "isSupportFullScreenChargeAnim" }) {
                    method { name = "isSupportFullScreenChargeAnim" }.hook {
                        before {
                            when (fullScreenChargeAnim) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                            }
                        }
                    }
                }
                //C13
                if (hasMethod { name = "isSupportShowWattage" }) {
                    method { name = "isSupportShowWattage" }.hook {
                        if (warpCharge == "2" && showWattage) replaceToTrue()
                    }
                }
                //C12 C13
                if (SDK == A13 && hasMethod { name = "isUseWarpCharge" }) {
                    method { name = "isUseWarpCharge" }.hook {
                        before {
                            when (warpCharge) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                            }
                        }
                    }
                }
                //C13 C14
                if (hasMethod { name = "isSupportMyDevice" }) {
                    method { name = "isSupportMyDevice" }.hook {
                        if (removeMyDevice) replaceToFalse()
                    }
                }
                //C12
                if (SDK < A13 && hasMethod { name = "isSupportLandClock" }) {
                    method { name = "isSupportLandClock" }.hook {
                        if (forceDisplayClockStyle) replaceToTrue()
                    }
                }
            }
        }
    }

    private object HookStatusBarFeature : YukiBaseHooker() {
        override fun onHook() {
            //隐藏未使用信号标签 config_isSystemUiExpSignalUi
            val hideSignalLabels =
                prefs(ModulePrefs).getBoolean("hide_inactive_signal_labels_gen2x2", false)

            //Source StatusBarFeatureOption
            VariousClass(
                "com.oplusos.systemui.statusbar.feature.StatusBarFeatureOption", //C13
                "com.oplusos.systemui.common.feature.StatusBarFeatureOption" //C14
            ).toClass().apply {
                method { name = "isSystemUiExpSignalUi" }.hook {
                    if (hideSignalLabels) replaceToTrue()
                }
            }
        }
    }

    private object HookNotificationAppFeature : YukiBaseHooker() {
        override fun onHook() {
            //通知重要性 com.android.systemui.origin_notification_behavior
            val notifyImportance = prefs(ModulePrefs).getBoolean(
                "enable_notification_importance_classification", false
            )
            //高斯模糊
            val enableBlur =
                prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)

            //Source NotificationAppFeatureOption
            VariousClass(
                "com.oplusos.systemui.common.util.NotificationAppFeatureOption", //C13
                "com.oplusos.systemui.common.feature.NotificationFeatureOption" //C14
            ).toClass().apply {
                method {
                    name = if (SDK >= A14) "isOriginNotificationBehavior"
                    else "originNotificationBehavior"
                }.hook {
                    if (notifyImportance) replaceToTrue()
                }

                //C12 C13 C14
                if (SDK >= A13) method {
                    name = if (SDK >= A14) "isGaussBlurDisabled"
                    else "getGaussBlurDisabled"
                }.hook {
                    if (enableBlur) replaceToFalse()
                }

                if (hasMethod { name = "isPanViewBlurDisabled" }) {
                    method { name = "isPanViewBlurDisabled" }.hook {
                        if (enableBlur) replaceToFalse()
                    }
                }
//                    method { name = "isAodMediaDisable" }
//                    afterHook {
//                        loggerD(msg = "isAodMediaDisable -> $result")
//                        resultFalse()
//                    }
            }
        }
    }

    private object HookFlavorOneFeature : YukiBaseHooker() {
        override fun onHook() {
            //全局搜索按钮
            val searchBtnMode =
                prefs(ModulePrefs).getString("set_control_center_search_button_mode", "0")
            //锁屏充电显示瓦数
            var showWattage =
                prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
            //应用专属媒体音量
            val specificVolume =
                prefs(ModulePrefs).getBoolean("enable_app_specific_media_volume", false)

            callback = { key: String, value: Any ->
                when (key) {
                    "force_lock_screen_charging_show_wattage" -> showWattage = value as Boolean
                }
            }

            //Source FlavorOneFeatureOption
            "com.oplusos.systemui.common.feature.FlavorOneFeatureOption".toClass().apply {
                if (hasMethod { name = "isSupportSearch" }) {
                    method { name = "isSupportSearch" }.hook {
                        before {
                            when (searchBtnMode) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                            }
                        }
                    }
                }
                //C14 Realme
                if (hasMethod { name = "isShowChargingWattage" }) {
                    method { name = "isShowChargingWattage" }.hook {
                        if (showWattage) replaceToTrue()
                    }
                }
                //C13.1 C14.0
                if (hasMethod { name = "isFlavorOneMultiMediaDevice" }) {
                    method { name = "isFlavorOneMultiMediaDevice" }.hook {
                        if (specificVolume) replaceToTrue()
                    }
                }
            }
        }
    }

    private object HookQSFeatureOption : YukiBaseHooker() {
        override fun onHook() {
            //自动亮度 com.android.systemui.remove_auto_brightness
            val autoBrightnessMode =
                prefs(ModulePrefs).getString("set_auto_brightness_button_mode", "0")

            //Source QSFeatureOption
            "com.oplusos.systemui.common.feature.QSFeatureOption".toClass().apply {
                method { name = "getShouldRemoveAutoBrightness" }.hook {
                    before {
                        when (autoBrightnessMode) {
                            "1" -> resultFalse()
                            "2" -> resultTrue()
                        }
                    }
                }
            }
        }
    }

    private object HookVolumeFeatureOption : YukiBaseHooker() {
        override fun onHook() {
            //音量对话框背景透明度
            var volumeBlur =
                prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)

            callback = { key: String, value: Any ->
                when (key) {
                    "custom_volume_dialog_background_transparency" -> volumeBlur = value as Int
                }
            }

            //Source VolumeFeatureOption
            "com.oplusos.systemui.common.feature.VolumeFeatureOption".toClass().apply {
                if (hasMethod { name = "isVolumeBlurDisabled" }) {
                    method { name = "isVolumeBlurDisabled" }.hook {
                        if (volumeBlur > -1) replaceToFalse()
                    }
                }
            }
        }
    }

    private object HookRegionalGaussBlurController : YukiBaseHooker() {
        override fun onHook() {
            //高斯模糊
            val enableBlur =
                prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)

            //Source RegionalGaussBlurController C12
            "com.oplusos.util.blur.RegionalGaussBlurController".toClass().apply {
                method { name = "getNormalBannerShouldDisableBlur" }.hook {
                    if (enableBlur) replaceToFalse()
                }
            }
        }
    }
}