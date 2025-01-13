package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class OplusBeaconLink : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.beaconlink")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusBeaconLink

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            if (osCode < 33) return@apply
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_beacon_link_time_limit)
                key = "remove_beacon_link_time_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}