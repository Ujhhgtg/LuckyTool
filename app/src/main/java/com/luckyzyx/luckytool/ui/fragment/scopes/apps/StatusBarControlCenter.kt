package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarControlCenter : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //时钟相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.ControlCenter_Clock_Related)
                key = "ControlCenter_Clock_Related"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.control_center_clock_show_second)
                key = "control_center_clock_show_second"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.statusbar_control_center_clock_red_one_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_control_center_clock_red_one_mode"
                entries =
                    resources.getStringArray(R.array.statusbar_control_center_clock_red_one_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.statusbar_control_center_clock_colon_style)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_control_center_clock_colon_style"
                entries =
                    resources.getStringArray(R.array.statusbar_control_center_clock_colon_style_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                isVisible = SDK >= A13
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_control_center_date_comma)
                key = "remove_control_center_date_comma"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_control_center_date_show_lunar)
                key = "statusbar_control_center_date_show_lunar"
                setDefaultValue(false)
                isVisible = isZh(context)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_control_center_date_disable_text_scroll)
                key = "statusbar_control_center_date_disable_text_scroll"
                setDefaultValue(false)
                isVisible = SDK >= A13 && isZh(context)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title =
                    getString(R.string.statusbar_control_center_date_set_display_mode_horizontal)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_control_center_date_set_display_mode_horizontal"
                entries =
                    resources.getStringArray(R.array.statusbar_control_center_date_fix_lunar_horizontal_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = SDK >= A13 && isZh(context) && context.getBoolean(
                    ModulePrefs, "statusbar_control_center_date_show_lunar", false
                )
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //通知中心
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.ControlCenterNotificationCenter)
                key = "ControlCenterNotificationCenter"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_notification_align_both_sides)
                key = "enable_notification_align_both_sides"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_notification_importance_classification)
                key = "enable_notification_importance_classification"
                setDefaultValue(false)
                isVisible = osCode < 30
                isIconSpaceReserved = false
            })
            addPreference(SeekBarPreference(context).apply {
                title = getString(R.string.custom_notification_background_transparency)
                summary = arraySummaryLine(
                    getString(R.string.force_enable_systemui_blur_feature_tips)
                )
                key = "custom_notification_background_transparency"
                setDefaultValue(-1)
                max = 10
                min = -1
                showSeekBarValue = true
                updatesContinuously = false
                isVisible = osCode >= 30
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_notification_background_blur_effect)
                summary = arraySummaryLine(
                    getString(R.string.force_enable_systemui_blur_feature_tips),
                    getString(R.string.force_enable_systemui_blur_feature_tips_2)
                )
                key = "enable_notification_background_blur_effect"
                setDefaultValue(false)
                isVisible = osCode >= 30
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //滑动条相关
            if (osCode >= 26) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.ControlCenter_Silder_Related)
                    key = "ControlCenter_Silder_Related"
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.custom_control_center_silder_transparency)
                    key = "custom_control_center_silder_transparency"
                    setDefaultValue(-1)
                    max = 10
                    min = -1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                })
            }
            //UI相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.ControlCenter_UI_Related)
                key = "ControlCenter_UI_Related"
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_control_center_volume_seekbar_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_control_center_volume_seekbar_mode"
                entries =
                    resources.getStringArray(R.array.set_control_center_volume_seekbar_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = osCode >= 31
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_auto_brightness_button_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_auto_brightness_button_mode"
                entries =
                    resources.getStringArray(R.array.statusbar_control_center_auto_brightness_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_control_center_user_switcher)
                key = "remove_control_center_user_switcher"
                setDefaultValue(false)
                isVisible = SDK < A13
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_control_center_mydevice)
                key = "remove_control_center_mydevice"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_control_center_search_button_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_control_center_search_button_mode"
                entries =
                    resources.getStringArray(R.array.set_control_center_search_button_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.remove_control_center_networkwarn)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.remove_control_center_networkwarn_summary)
                )
                key = "remove_control_center_networkwarn"
                entries =
                    resources.getStringArray(R.array.statusbar_control_center_networkwarn_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SeekBarPreference(context).apply {
                title = getString(R.string.custom_control_center_background_transparency)
                summary = getString(R.string.force_enable_systemui_blur_feature_tips)
                key = "custom_control_center_background_transparency"
                setDefaultValue(-1)
                max = 10
                min = -1
                showSeekBarValue = true
                updatesContinuously = false
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}