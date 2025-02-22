package com.luckyzyx.luckytool.ui.fragment.scopes.statusbar

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.getBoolean

@Obfuscate
class StatusBarLayout : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.statusBarLayout

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            title = getString(R.string.StatusBarLayout)
            summary = arraySummaryDot(
                getString(R.string.statusbar_layout_mode),
                getString(R.string.statusbar_layout_compatible_mode)
            )
            key = "StatusBarLayout"
            isIconSpaceReserved = false
            isVisible = SDK == A13
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.statusbar_layout_mode)
                summary =
                    getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_layout_mode"
                setEntries(R.array.statusbar_layout_mode_entries)
                entryValues = arrayOf("0", "1")
                setDefaultValue("0")
                isVisible = SDK == A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.statusbar_layout_compatible_mode)
                key = "statusbar_layout_compatible_mode"
                setDefaultValue(false)
                isVisible = SDK == A13
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (SDK == A13 &&
                getBoolean(ModulePrefs, "statusbar_layout_compatible_mode", false)
            ) {
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_layout_left_margin)
                    summary = getString(R.string.statusbar_layout_margin_tip)
                    key = "statusbar_layout_left_margin"
                    setDefaultValue(0)
                    max = 150
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                })
                add(SeekBarPreference(this@loadPreferences).apply {
                    title = getString(R.string.statusbar_layout_right_margin)
                    summary = getString(R.string.statusbar_layout_margin_tip)
                    key = "statusbar_layout_right_margin"
                    setDefaultValue(0)
                    max = 150
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                })
            }
        }
    }
}