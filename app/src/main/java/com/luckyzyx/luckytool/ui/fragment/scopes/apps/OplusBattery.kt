package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getBoolean

@Obfuscate
class OplusBattery : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.battery")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            if (SDK >= A13) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.open_battery_health)
                    summary = getString(R.string.open_battery_health_summary)
                    key = "open_battery_health"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getBoolean(ModulePrefs, "open_battery_health", false)) {
                    addPreference(EditTextPreference(context).apply {
                        title = getString(R.string.customize_battery_health_data_percentage)
                        dialogTitle = title
                        key = "customize_battery_health_data_percentage"
                        setDefaultValue("")
                        setSummaryProvider {
                            EditTextPreference.SimpleSummaryProvider.getInstance()
                                .provideSummary(this)
                        }
                        isIconSpaceReserved = false
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.display_module_calculates_battery_health_data)
                        summary =
                            getString(R.string.display_module_calculates_battery_health_data_summary)
                        key = "display_module_calculates_battery_health_data"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                }
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_stop_charging_at_80)
                    key = "enable_stop_charging_at_80"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.open_screen_power_save)
                    summary = getString(R.string.open_screen_power_save_summary)
                    key = "open_screen_power_save"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_battery_temperature_control)
                summary = getString(R.string.remove_battery_temperature_control_summary)
                key = "remove_battery_temperature_control"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //电池优化
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.BatteryOptimization)
                key = "BatteryOptimization"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.restore_default_battery_optimization_whitelist)
                key = "restore_default_battery_optimization_whitelist"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = IntentUtils(requireActivity()).jumpBattery()
}