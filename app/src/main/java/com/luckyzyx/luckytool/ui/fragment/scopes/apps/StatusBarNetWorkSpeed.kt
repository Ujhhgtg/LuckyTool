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
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarNetWorkSpeed : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.set_network_speed)
                key = "set_network_speed"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.statusbar_network_layout)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "statusbar_network_layout"
                setEntries(R.array.statusbar_network_layout_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.use_user_typeface)
                key = "statusbar_network_user_typeface"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.use_bold_font_style)
                key = "statusbar_network_use_bold_font_style"
                setDefaultValue(false)
                isVisible = context.getBoolean(
                    ModulePrefs, "statusbar_network_user_typeface", false
                )
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            if (context.getString(ModulePrefs, "statusbar_network_layout", "0") != "0") {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_network_no_second)
                    key = "statusbar_network_no_second"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.statusbar_network_no_space)
                    key = "statusbar_network_no_space"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.set_network_speed_font_size)
                    key = "set_network_speed_font_size"
                    setDefaultValue(7)
                    max = 10
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
                    title = getString(R.string.set_network_speed_padding_bottom)
                    key = "set_network_speed_padding_bottom"
                    setDefaultValue(0)
                    max = 6
                    min = 0
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        true
                    }
                })
                if (context.getString(ModulePrefs, "statusbar_network_layout", "0") == "2") {
                    addPreference(SeekBarPreference(context).apply {
                        title = getString(R.string.set_network_speed_double_row_spacing)
                        key = "set_network_speed_double_row_spacing"
                        setDefaultValue(-1)
                        max = 6
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
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}