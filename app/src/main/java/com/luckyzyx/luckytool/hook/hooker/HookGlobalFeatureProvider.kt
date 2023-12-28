package com.luckyzyx.luckytool.hook.hooker

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.android.HookAppFeatureProvider
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class HookGlobalFeatureProvider(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            //Source SystemUI AutoBrightnessTile 自动亮度
            when (prefs(ModulePrefs).getString("set_auto_brightness_button_mode", "0")) {
                "1" -> put("com.android.systemui.remove_auto_brightness", false)
                "2" -> put("com.android.systemui.remove_auto_brightness", true)
            }
            //Source SystemUI NotificationFeatureOption 通知重要性
            val notifyImportance = prefs(ModulePrefs).getBoolean(
                "enable_notification_importance_classification", false
            )
            if (notifyImportance) {
                put("com.android.systemui.origin_notification_behavior", true)
            }
            //Source SystemUI OpAssistNavigationDialog 音量条位置
            when (prefs(ModulePrefs).getString("set_volume_bar_display_position", "0")) {
                "1" -> put("com.android.systemui.volume_and_power_key_in_right", false)
                "2" -> put("com.android.systemui.volume_and_power_key_in_right", true)
            }

            //Source Settings OplusDefaultAutofillPicker -> autofill_password 自动填充密码
            if (prefs(ModulePrefs).getBoolean("disable_cn_special_edition_setting", false)) {
                put("com.android.settings.cn_version", false)
            }
            //Source Settings DisplayTimeOutController -> 永不息屏(24H)
            if (prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)) {
                put("com.android.settings.show_never_timeout", true)
            }
            val processorDetail = prefs(ModulePrefs).getString("set_processor_click_page", "0")
            //Source Settings com.android.settings.processor_detail
            if (processorDetail != "0") put("com.android.settings.processor_detail", true)
            //Source Settings com.android.settings.processor_detail_gen2
            if (processorDetail == "2") put("com.android.settings.processor_detail_gen2", true)
            //Source Settings com.android.settings.ultimate_cleanup
            if (prefs(ModulePrefs).getBoolean("force_display_process_management", false)) {
                put("com.android.settings.ultimate_cleanup", true)
            }
            //Source Settings DeviceInfoUtils 屏幕尺寸显示厘米
            if (prefs(ModulePrefs).getBoolean("screen_physics_size_shown_cm", false)) {
                put("com.android.settings.screen_physics_size_cm", true)
            }

            //Source Battery 屏幕省电
            if (prefs(ModulePrefs).getBoolean("open_screen_power_save", false)) {
                put("com.oplus.battery.cabc_level_dynamic_enable", true)
            }
            //Source Battery 电池健康
            if (prefs(ModulePrefs).getBoolean("open_battery_health", false)) {
                put("os.charge.settings.batterysettings.batteryhealth", true)
            }
        }
        loadHooker(HookAppFeatureProvider(dexKitBridge, list))
    }
}