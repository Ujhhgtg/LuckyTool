package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusMcs : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.heytap.mcs")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusMcs

    private val regions = arrayOf("", "CN", "IN", "US")

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.heytap.mcs"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.custom_system_message_region_defaults),
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.custom_system_message_region_defaults)
                summary = getString(R.string.current_mode) + ": %s"
                key = "custom_system_message_region_defaults"
                setEntries(R.array.custom_system_message_region_defaults_entries)
                entryValues = regions
                setDefaultValue(regions.first())
                isIconSpaceReserved = false
            })
        }
    }
}