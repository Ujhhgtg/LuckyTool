package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class LockScreen : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui", "com.oplus.notificationmanager")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //状态栏组件
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.LockScreenStatusBar)
                key = "LockScreenStatusBar"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.hide_lock_screen_status_bar_display)
                key = "hide_lock_screen_status_bar_display"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_statusbar_carriers)
                key = "remove_statusbar_carriers"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(EditTextPreference(context).apply {
                title = getString(R.string.statusbar_custom_carrier_display_text)
                dialogTitle = title
                key = "statusbar_custom_carrier_display_text"
                setDefaultValue("")
                setSummaryProvider {
                    EditTextPreference.SimpleSummaryProvider.getInstance().provideSummary(this)
                }
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_carriers_use_user_typeface)
                key = "statusbar_carriers_use_user_typeface"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //时钟组件
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.LockScreenClockComponent)
                key = "LockScreenClockComponent"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_lock_screen_clock_component)
                key = "remove_lock_screen_clock_component"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(ModulePrefs, "remove_lock_screen_clock_component").not()) {
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.lock_screen_clock_redone_mode)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "lock_screen_clock_redone_mode"
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
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.apply_lock_screen_dual_clock_redone)
                    key = "apply_lock_screen_dual_clock_redone"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.lock_screen_custom_clock_component_style)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "lock_screen_custom_clock_component_style"
                    entries =
                        resources.getStringArray(R.array.lock_screen_custom_clock_component_style_entries)
                    entryValues = arrayOf("0", "1", "2")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.force_display_clock_style_options)
                    summary = getString(R.string.force_display_clock_style_options_summary)
                    key = "force_display_clock_style_options"
                    setDefaultValue(false)
                    isVisible = context.getString(
                        ModulePrefs, "lock_screen_custom_clock_component_style", "0"
                    ) == "1"
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.set_lock_screen_centered)
                    summary = getString(R.string.set_lock_screen_centered_summary)
                    key = "set_lock_screen_centered"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.lock_screen_clock_use_user_typeface)
                    key = "lock_screen_clock_use_user_typeface"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            //充电组件
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.LockScreenChargingComponent)
                key = "LockScreenChargingComponent"
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_lock_screen_warp_charging_style)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_lock_screen_warp_charging_style"
                entries =
                    resources.getStringArray(R.array.set_lock_screen_warp_charging_style_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = SDK == A13
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_lock_screen_charging_text_logo_style)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_lock_screen_charging_text_logo_style"
                entries =
                    resources.getStringArray(R.array.set_lock_screen_charging_text_logo_style_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.lock_screen_show_real_charging_technology)
                key = "lock_screen_show_real_charging_technology"
                setDefaultValue(false)
                isVisible = SDK >= A13 && context.getString(
                    ModulePrefs, "set_lock_screen_charging_text_logo_style"
                ) != "2"
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_lock_screen_charging_show_wattage)
                key = "force_lock_screen_charging_show_wattage"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.lock_screen_charging_use_user_typeface)
                key = "lock_screen_charging_use_user_typeface"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_full_screen_charging_animation_mode)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.need_restart_scope)
                )
                key = "set_full_screen_charging_animation_mode"
                entries =
                    resources.getStringArray(R.array.set_full_screen_charging_animation_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = getOSVersionCode in 27..29
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //锁屏按钮
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.LockScreenButton)
                key = "LockScreenButton"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_top_lock_screen_icon)
                key = "remove_top_lock_screen_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_lock_screen_bottom_left_button)
                key = "remove_lock_screen_bottom_left_button"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.lock_screen_bottom_left_button_replace_with_flashlight)
                key = "lock_screen_bottom_left_button_replace_with_flashlight"
                setDefaultValue(false)
                isVisible = SDK < A14 && context.getBoolean(
                    ModulePrefs, "remove_lock_screen_bottom_left_button", false
                ) == false
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.lock_screen_switch_flashlight_auto_close_screen)
                key = "lock_screen_switch_flashlight_auto_close_screen"
                setDefaultValue(false)
                isVisible = context.getBoolean(
                    ModulePrefs, "remove_lock_screen_bottom_left_button", false
                ) == false
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_lock_screen_bottom_right_camera)
                key = "remove_lock_screen_bottom_right_camera"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_lock_screen_close_notification_button)
                key = "remove_lock_screen_close_notification_button"
                setDefaultValue(false)
                isVisible = osCode < 33
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_lock_screen_bottom_sos_button)
                summary = getString(R.string.remove_lock_screen_bottom_sos_button_summary)
                key = "remove_lock_screen_bottom_sos_button"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            //锁屏事件
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.LockScreenEvent)
                key = "LockScreenEvent"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_72hour_password_verification)
                summary = getString(R.string.remove_72hour_password_verification_summary)
                key = "remove_72hour_password_verification"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}