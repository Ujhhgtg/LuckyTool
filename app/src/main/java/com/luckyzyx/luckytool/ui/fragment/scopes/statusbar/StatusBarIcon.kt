package com.luckyzyx.luckytool.ui.fragment.scopes.statusbar

import android.content.Context
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarIcon : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.statusBarIcon

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            //WIFI
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusBarWIFIIcon)
                key = "StatusBarWIFIIcon"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_wifi_data_inout)
                key = "remove_wifi_data_inout"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //移动数据
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusBarMobileDataIcon)
                key = "StatusBarMobileDataIcon"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_mobile_data_inout)
                key = "remove_mobile_data_inout"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_mobile_data_type)
                key = "remove_mobile_data_type"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.hide_non_network_card_icon)
                key = "hide_non_network_card_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.hide_inactive_signal_labels_gen2x2)
                key = "hide_inactive_signal_labels_gen2x2"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.hide_nosim_noservice)
                key = "hide_nosim_noservice"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //蓝牙
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusBarBluetoothIcon)
                key = "StatusBarBluetoothIcon"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.hide_icon_when_bluetooth_not_connected)
                key = "hide_icon_when_bluetooth_not_connected"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //其他
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusBarOtherIcon)
                key = "StatusBarOtherIcon"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_high_performance_mode_icon)
                key = "remove_high_performance_mode_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_statusbar_securepayment_icon)
                key = "remove_statusbar_securepayment_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_green_dot_privacy_prompt)
                key = "remove_green_dot_privacy_prompt"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_system_prompt_icon)
                summary = getString(R.string.remove_system_prompt_icon_summary)
                key = "remove_system_prompt_icon"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SeekBarPreference(this@loadPreferences).apply {
                title = getString(R.string.custom_fluid_cloud_icon_background_transparency)
                key = "custom_fluid_cloud_icon_background_transparency"
                setDefaultValue(-1)
                max = 10
                min = -1
                isVisible = osCode >= 30
                showSeekBarValue = true
                updatesContinuously = false
                isVisible = osCode in 30..33
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            //图标状态
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusBarSmallIconStatus)
                key = "StatusBarSmallIconStatus"
                isVisible = SDK <= A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.status_bar_icon_vertical_center)
                key = "status_bar_icon_vertical_center"
                setDefaultValue(false)
                isVisible = SDK <= A13
                isIconSpaceReserved = false
            })
        }
    }
}