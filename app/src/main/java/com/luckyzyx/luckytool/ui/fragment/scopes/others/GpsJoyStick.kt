package com.luckyzyx.luckytool.ui.fragment.scopes.others

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.openApp
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class GpsJoyStick : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.theappninjas.fakegpsjoystick")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.gpsJoyStick

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.theappninjas.fakegpsjoystick"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.adm_unlock_pro)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.adm_unlock_pro)
                key = "gps_joystick_unlock_pro"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}