package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.luckytool.utils.setSummaryProvider

@Obfuscate
class OplusBattery : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.battery")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusBattery

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.battery"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.open_battery_health),
                getString(R.string.remove_battery_temperature_control)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            if (SDK >= A13) {
                add(SwitchPreference(this@loadPreferences).apply {
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
                if (getBoolean(ModulePrefs, "open_battery_health", false)) {
                    add(EditTextPreference(this@loadPreferences).apply {
                        title = getString(R.string.customize_battery_health_data_percentage)
                        dialogTitle = title
                        key = "customize_battery_health_data_percentage"
                        setDefaultValue("")
                        setSummaryProvider(this)
                        isIconSpaceReserved = false
                    })
                    add(SwitchPreference(this@loadPreferences).apply {
                        title = getString(R.string.display_module_calculates_battery_health_data)
                        summary =
                            getString(R.string.display_module_calculates_battery_health_data_summary)
                        key = "display_module_calculates_battery_health_data"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                }
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_stop_charging_at_80)
                    key = "enable_stop_charging_at_80"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.open_screen_power_save)
                    summary = getString(R.string.open_screen_power_save_summary)
                    key = "open_screen_power_save"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_battery_temperature_control)
                summary = getString(R.string.remove_battery_temperature_control_summary)
                key = "remove_battery_temperature_control"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //电池优化
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.BatteryOptimization)
                key = "BatteryOptimization"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.restore_default_battery_optimization_whitelist)
                key = "restore_default_battery_optimization_whitelist"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = IntentUtils(requireActivity()).jumpBattery()
}