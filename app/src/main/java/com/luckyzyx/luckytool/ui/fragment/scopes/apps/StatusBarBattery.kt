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
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarBattery : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_statusbar_battery_percent)
                key = "remove_statusbar_battery_percent"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.use_user_typeface)
                key = "statusbar_power_user_typeface"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(ModulePrefs, "statusbar_power_user_typeface", false)) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.use_bold_font_style)
                    key = "statusbar_power_use_bold_font_style"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.statusbar_power_font_size)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_power_font_size"
                    setDefaultValue(0)
                    max = 15
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                })
            }
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_power_apply_to_battery_icon)
                key = "statusbar_power_apply_to_battery_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //状态栏电池通知
            if (SDK >= A12) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.StatusBarBatteryNotify)
                    key = "StatusBarBatteryNotify"
                    isIconSpaceReserved = false
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.battery_information_display_mode)
                    summary = arraySummaryLine(
                        getString(R.string.common_words_current_mode) + ": %s",
                        getString(R.string.battery_information_display_mode_summary)
                    )
                    key = "battery_information_display_mode"
                    entries =
                        resources.getStringArray(R.array.statusbar_battery_information_notify_entries)
                    entryValues = arrayOf("0", "1", "2")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getString(
                        ModulePrefs, "battery_information_display_mode", "0"
                    ) != "0"
                ) {
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.battery_information_show_charge)
                        summary = getString(R.string.battery_information_show_charge_summary)
                        key = "battery_information_show_charge_info"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(DropDownPreference(context).apply {
                        title = getString(R.string.battery_information_voltage_display_mode)
                        summary =
                            arraySummaryLine(getString(R.string.common_words_current_mode) + ": %s")
                        key = "battery_information_voltage_display_mode"
                        entries =
                            resources.getStringArray(R.array.battery_information_voltage_display_mode_entries)
                        entryValues = arrayOf("0", "1", "2")
                        setDefaultValue("0")
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.battery_information_show_battery_health)
                        key = "battery_information_show_battery_health"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.battery_information_always_show_positive_current)
                        key = "battery_information_always_show_positive_current"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.battery_information_show_simple_mode)
                        key = "battery_information_show_simple_mode"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.battery_information_show_update_time)
                        summary = getString(R.string.battery_information_show_update_time_summary)
                        key = "battery_information_show_update_time"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    addPreference(SeekBarPreference(context).apply {
                        title = getString(R.string.battery_information_custom_font_size)
                        key = "battery_information_custom_font_size"
                        setDefaultValue(11)
                        max = 20
                        min = 11
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
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}