package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusTeleService : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.phone", "com.android.incallui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusTeleService

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.android.phone"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.force_display_five_g_switch),
                getString(R.string.force_display_preferred_network_type)
            )
            isVisible = SDK >= A13 && checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_display_five_g_switch)
                key = "force_display_five_g_switch"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_display_volte_calls)
                key = "force_display_volte_calls"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_display_preferred_network_type)
                key = "force_display_preferred_network_type"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_sound_sealed_call)
                key = "enable_sound_sealed_call"
                setDefaultValue(false)
                isVisible = SDK >= A14
                isIconSpaceReserved = false
            })
        }
    }
}