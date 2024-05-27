package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarClock : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.statusbar_clock_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_clock_mode"
                setEntries(R.array.statusbar_clock_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getString(ModulePrefs, "statusbar_clock_mode", "0") == "1") {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_year)
                    key = "statusbar_clock_show_year"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_month)
                    key = "statusbar_clock_show_month"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_day)
                    key = "statusbar_clock_show_day"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_week)
                    key = "statusbar_clock_show_week"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_period)
                    key = "statusbar_clock_show_period"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_double_hour)
                    key = "statusbar_clock_show_double_hour"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_second)
                    key = "statusbar_clock_show_second"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_hide_spaces)
                    key = "statusbar_clock_hide_spaces"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_clock_show_doublerow)
                    key = "statusbar_clock_show_doublerow"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.statusbar_clock_text_alignment)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "statusbar_clock_text_alignment"
                    entries =
                        resources.getStringArray(R.array.statusbar_clock_text_alignment_entries)
                    entryValues = arrayOf("left", "center", "right")
                    setDefaultValue("center")
                    isVisible =
                        context.getBoolean(ModulePrefs, "statusbar_clock_show_doublerow", false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.statusbar_clock_singlerow_fontsize)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_clock_singlerow_fontsize"
                    setDefaultValue(0)
                    max = 28
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.statusbar_clock_doublerow_fontsize)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_clock_doublerow_fontsize"
                    setDefaultValue(0)
                    max = 20
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
            }
            if (context.getString(ModulePrefs, "statusbar_clock_mode", "0") == "2") {
                addPreference(EditTextPreference(context).apply {
                    title = getString(R.string.statusbar_clock_custom_format)
                    dialogTitle = getString(R.string.statusbar_clock_custom_format)
                    dialogMessage = """
                            YYYY/MM/dd -> ${formatDate("YYYY/MM/dd")}
                            Y/M/d/E/a -> ${formatDate("Y/M/d/E/a")}
                            YY/YYYY -> ${formatDate("YY/YYYY")}
                            M/MM/MMM/MMMM/MMMMM -> ${formatDate("M/MM/MMM/MMMM/MMMMM")}
                            d/dd/ddd/dddd -> ${formatDate("d/dd/d号/dd号")}
                            E/EE/EEE/EEEE/EEEEE -> ${formatDate("E/EE/EEE/EEEE/EEEEE")}
                            h/H/k/K -> ${formatDate("h/H/k/K")}
                            HH:mm:ss -> ${formatDate("HH:mm:ss")}
                            m/mm/mmm/mmmm -> ${formatDate("m/mm/mmm/mmmm")}
                            s/ss/sss/ssss -> ${formatDate("s/ss/sss/ssss")}
                            z -> ${formatDate("z")}
                            G -> ${formatDate("G")}
                            GG -> 子时/丑时/寅时/卯时
                            N -> 初一
                            NN -> 二月初一
                            NNN -> 兔年二月初一
                            NNNN -> 癸卯兔年二月初一
                            FF -> 凌晨/上午/傍晚/晚上
                        """.trimIndent()
                    key = "statusbar_clock_custom_format"
                    setDefaultValue("HH:mm:ss")
                    setSummaryProvider {
                        EditTextPreference.SimpleSummaryProvider.getInstance().provideSummary(this)
                    }
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        (activity as MainActivity).restart()
                        true
                    }
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.statusbar_clock_text_alignment)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "statusbar_clock_text_alignment"
                    entries =
                        resources.getStringArray(R.array.statusbar_clock_text_alignment_entries)
                    entryValues = arrayOf("left", "center", "right")
                    setDefaultValue("center")
                    val row = context.getString(
                        ModulePrefs, "statusbar_clock_custom_format", "HH:mm:ss"
                    ).split("\n").size
                    isVisible = row >= 2
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.statusbar_clock_custom_fontsize)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_clock_custom_fontsize"
                    setDefaultValue(0)
                    max = 30
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
            }
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.use_user_typeface)
                key = "statusbar_clock_user_typeface"
                setDefaultValue(false)
                isVisible = context.getString(ModulePrefs, "statusbar_clock_mode", "0") != "0"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.use_bold_font_style)
                key = "statusbar_clock_use_bold_font_style"
                setDefaultValue(false)
                isVisible = context.getBoolean(
                    ModulePrefs, "statusbar_clock_user_typeface", false
                )
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