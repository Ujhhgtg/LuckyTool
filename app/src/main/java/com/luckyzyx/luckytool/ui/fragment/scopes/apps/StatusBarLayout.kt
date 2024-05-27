package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getBoolean

@Obfuscate
class StatusBarLayout : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.statusbar_layout_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_layout_mode"
                setEntries(R.array.statusbar_layout_mode_entries)
                entryValues = arrayOf("0", "1")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_layout_compatible_mode)
                key = "statusbar_layout_compatible_mode"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(
                    ModulePrefs, "statusbar_layout_compatible_mode", false
                )
            ) {
                addPreference(SeekBarPreference(context).apply {
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
                addPreference(SeekBarPreference(context).apply {
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

    override fun isEnableRestartMenu(): Boolean = true
}