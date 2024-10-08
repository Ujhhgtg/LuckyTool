package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class DialogRelated : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui", "com.oplus.exsystemservice")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.dialogRelated

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.disable_duplicate_floating_window)
                summary = getString(R.string.disable_duplicate_floating_window_summary)
                key = "disable_duplicate_floating_window"
                setDefaultValue(false)
                isVisible = getOSVersionCode >= 26
                isIconSpaceReserved = false

            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_low_battery_dialog_warning)
                summary = getString(R.string.remove_low_battery_dialog_warning_summary)
                key = "remove_low_battery_dialog_warning"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_usb_connect_dialog)
                summary = getString(R.string.remove_usb_connect_dialog_summary)
                key = "remove_usb_connect_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_access_device_log_dialog)
                key = "remove_access_device_log_dialog"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.run_floating_window_tasks_in_foreground)
                key = "run_floating_window_tasks_in_foreground"
                setDefaultValue(false)
                isVisible = osCode >= 26
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    sendPrefsValue("com.android.systemui", key, newValue)
                    true
                }
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.auto_tap_start_recording_or_casting_dialog)
                key = "auto_tap_start_recording_or_casting_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
//            add(SwitchPreference(this@loadPreferences).apply {
//                title = getString(R.string.reduce_power_menu_display_delay)
//                key = "reduce_power_menu_display_delay"
//                setDefaultValue(false)
//                isVisible = false
//                isIconSpaceReserved = false
//            })
        }
    }
}