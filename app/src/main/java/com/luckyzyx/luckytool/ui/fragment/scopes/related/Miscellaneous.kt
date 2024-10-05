package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.navigatePage

@Obfuscate
class Miscellaneous : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.systemui",
        "com.android.externalstorage",
        "com.oplus.exsystemservice",
        "com.coloros.securepay"
    )

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.miscellaneous

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.FloatingWindowDialogRelated)
                summary = arraySummaryDot(
                    getString(R.string.remove_low_battery_dialog_warning_summary),
                    getString(R.string.disable_headphone_high_volume_warning)
                )
                key = "FloatingWindowDialogRelated"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.dialogRelated, title)
                    true
                }
            })
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.FingerPrintRelated)
                summary = arraySummaryDot(
                    getString(R.string.remove_fingerprint_icon),
                    getString(R.string.replace_fingerprint_icon_switch)
                )
                key = "FingerPrintRelated"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.fingerPrintRelated, title)
                    true
                }
            })
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.SoundRelated)
                summary = arraySummaryDot(
                    getString(R.string.media_volume_level),
                    getString(R.string.minimum_volume_level_can_be_zero)
                )
                key = "SoundRelated"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.soundRelated, title)
                    true
                }
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.show_charging_ripple)
                summary = getString(R.string.show_charging_ripple_summary)
                key = "show_charging_ripple"
                setDefaultValue(false)
                isVisible = SDK >= A12
                isIconSpaceReserved = false
            })
            if (osCode < 30) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.disable_otg_auto_off)
                    summary = getString(R.string.disable_otg_auto_off_summary)
                    key = "disable_otg_auto_off"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_storage_limit)
                summary = getString(R.string.remove_storage_limit_summary)
                key = "remove_storage_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_enable_systemui_blur_feature)
                key = "force_enable_systemui_blur_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.show_manual_lock_button_power_menu)
                key = "show_manual_lock_button_power_menu"
                setDefaultValue(false)
                isVisible = SDK >= A14
                isIconSpaceReserved = false
            })
        }
    }
}