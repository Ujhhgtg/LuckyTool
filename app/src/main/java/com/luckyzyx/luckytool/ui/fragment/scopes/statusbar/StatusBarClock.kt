package com.luckyzyx.luckytool.ui.fragment.scopes.statusbar

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue
import com.luckyzyx.luckytool.utils.setSummaryProvider
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class StatusBarClock : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.statusBarClock

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            title = getString(R.string.StatusBarClock)
            summary = arraySummaryDot(
                getString(R.string.statusbar_clock_show_second),
                getString(R.string.statusbar_clock_show_doublerow),
                getString(
                    R.string.statusbar_clock_doublerow_fontsize
                )
            )
            key = "StatusBarClock"
            isIconSpaceReserved = false
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(DropDownPreference(this@loadPreferences).apply {
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
            if (getString(ModulePrefs, "statusbar_clock_mode", "0") == "1") {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_year)
                    key = "statusbar_clock_show_year"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_month)
                    key = "statusbar_clock_show_month"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_day)
                    key = "statusbar_clock_show_day"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_week)
                    key = "statusbar_clock_show_week"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_period)
                    key = "statusbar_clock_show_period"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_double_hour)
                    key = "statusbar_clock_show_double_hour"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_second)
                    key = "statusbar_clock_show_second"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_hide_spaces)
                    key = "statusbar_clock_hide_spaces"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_show_doublerow)
                    key = "statusbar_clock_show_doublerow"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                add(DropDownPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_text_alignment)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "statusbar_clock_text_alignment"
                    setEntries(R.array.statusbar_clock_text_alignment_entries)
                    entryValues = arrayOf("left", "center", "right")
                    setDefaultValue("center")
                    isVisible =
                        getBoolean(ModulePrefs, "statusbar_clock_show_doublerow", false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_singlerow_fontsize)
                    summary = getString(R.string.statusbar_clock_if_zero_summary)
                    key = "statusbar_clock_singlerow_fontsize"
                    setDefaultValue(0)
                    max = 28
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_doublerow_fontsize)
                    summary = getString(R.string.statusbar_clock_if_zero_summary)
                    key = "statusbar_clock_doublerow_fontsize"
                    setDefaultValue(0)
                    max = 20
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
            }
            if (getString(ModulePrefs, "statusbar_clock_mode", "0") == "2") {
                add(EditTextPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_custom_format)
                    dialogTitle = title
                    dialogMessage = """
                            yyyy/MM/dd -> ${formatDate("yyyy/MM/dd")}
                            y/M/d/E/a -> ${formatDate("y/M/d/E/a")}
                            yy/yyyy -> ${formatDate("yy/yyyy")}
                            M/MM/MMM/MMMM/MMMMM -> ${formatDate("M/MM/MMM/MMMM/MMMMM")}
                            d/dd/ddd/dddd -> ${formatDate("d/dd/d号/dd号")}
                            E/EE/EEE/EEEE/EEEEE -> ${formatDate("E/EE/EEE/EEEE/EEEEE")}
                            H/HH (0-23) k/kk (1-24) -> ${formatDate("H/HH k/kk")}
                            K/KK (0-11) h/hh (1-12) -> ${formatDate("K/KK h/hh")}
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
                    setSummaryProvider(this)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        (activity as MainActivity).restart()
                        true
                    }
                })
                add(DropDownPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_text_alignment)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "statusbar_clock_text_alignment"
                    setEntries(R.array.statusbar_clock_text_alignment_entries)
                    entryValues = arrayOf("left", "center", "right")
                    setDefaultValue("center")
                    val row = getString(
                        ModulePrefs, "statusbar_clock_custom_format", "HH:mm:ss"
                    ).split("\n").size
                    isVisible = row >= 2
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_custom_fontsize)
                    summary = getString(R.string.statusbar_clock_if_zero_summary)
                    key = "statusbar_clock_custom_fontsize"
                    setDefaultValue(0)
                    max = 30
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
            }
            if (getString(ModulePrefs, "statusbar_clock_mode", "0") != "0") {
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_custom_minimum_width)
                    summary = getString(R.string.statusbar_clock_if_zero_summary)
                    key = "statusbar_clock_custom_minimum_width"
                    setDefaultValue(0)
                    max = 50
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_clock_custom_padding)
                    key = "statusbar_clock_custom_padding"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (getBoolean(ModulePrefs, "statusbar_clock_custom_padding", false)) {
                    add(SeekBarPreference(this@loadPreferences).apply {
                        title = getString(R.string.statusbar_clock_custom_top_padding)
                        summary = getString(R.string.statusbar_clock_if_zero_summary)
                        key = "statusbar_clock_custom_top_padding"
                        setDefaultValue(0)
                        max = 30
                        min = -30
                        showSeekBarValue = true
                        updatesContinuously = false
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    add(SeekBarPreference(this@loadPreferences).apply {
                        title = getString(R.string.statusbar_clock_custom_bottom_padding)
                        summary = getString(R.string.statusbar_clock_if_zero_summary)
                        key = "statusbar_clock_custom_bottom_padding"
                        setDefaultValue(0)
                        max = 30
                        min = -30
                        showSeekBarValue = true
                        updatesContinuously = false
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    add(SeekBarPreference(this@loadPreferences).apply {
                        title = getString(R.string.statusbar_clock_custom_left_padding)
                        summary = getString(R.string.statusbar_clock_if_zero_summary)
                        key = "statusbar_clock_custom_left_padding"
                        setDefaultValue(0)
                        max = 30
                        min = -30
                        showSeekBarValue = true
                        updatesContinuously = false
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                    add(SeekBarPreference(this@loadPreferences).apply {
                        title = getString(R.string.statusbar_clock_custom_right_padding)
                        summary = getString(R.string.statusbar_clock_if_zero_summary)
                        key = "statusbar_clock_custom_right_padding"
                        setDefaultValue(0)
                        max = 30
                        min = -30
                        showSeekBarValue = true
                        updatesContinuously = false
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                }
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.use_user_typeface)
                    key = "statusbar_clock_user_typeface"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (getBoolean(ModulePrefs, "statusbar_clock_user_typeface", false)) {
                    add(SwitchPreference(this@loadPreferences).apply {
                        title = getString(R.string.use_bold_font_style)
                        key = "statusbar_clock_use_bold_font_style"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            sendPrefsValue("com.android.systemui", key, newValue)
                            true
                        }
                    })
                }
            }
        }
    }
}