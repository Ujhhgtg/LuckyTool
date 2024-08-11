package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getString

@Obfuscate
class Android : BaseScopePreferenceFeagment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.allow_untrusted_touch)
                summary = getString(R.string.allow_untrusted_touch_summary)
                key = "allow_untrusted_touch"
                setDefaultValue(false)
                isVisible = SDK >= A12
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.disable_temperature_control_listener)
                summary = getString(R.string.need_restart_system)
                key = "disable_temperature_control_listener"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.customized_gaussian_blur_effect_level)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.need_restart_system)
                )
                key = "customized_gaussian_blur_effect_level"
                entries =
                    resources.getStringArray(R.array.customized_gaussian_blur_effect_level_entries)
                entryValues = arrayOf("-1", "0", "1", "2", "3")
                setDefaultValue("-1")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            //LTPO
            addPreference(PreferenceCategory(context).apply {
                title = "LTPO"
                key = "OplusLTPO"
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_ltpo_refresh_rate_mode)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.need_restart_system)
                )
                key = "set_ltpo_refresh_rate_mode"
                entries = resources.getStringArray(R.array.set_ltpo_refresh_rate_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getString(ModulePrefs, "set_ltpo_refresh_rate_mode", "0") == "1") {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_full_brightness_refresh_rate_minimum_one)
                    summary = arraySummaryLine(
                        getString(R.string.enable_full_brightness_refresh_rate_minimum_one_summary),
                        getString(R.string.need_restart_system)
                    )
                    key = "enable_full_brightness_refresh_rate_minimum_one"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
        }
    }
}