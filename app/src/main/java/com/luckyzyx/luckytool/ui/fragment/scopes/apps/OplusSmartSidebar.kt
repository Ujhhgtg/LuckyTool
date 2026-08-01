package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusSmartSidebar : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.smartsidebar")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusSmartSidebar

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.smartsidebar"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.enable_run_in_background),
            )
            isVisible = SDK >= A12 && checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_enable_buoy_automatically_hides)
                key = "force_enable_buoy_automatically_hides"
                setDefaultValue(false)
                isVisible = SDK == A12
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.unlock_transfer_dock)
                key = "unlock_transfer_dock"
                setDefaultValue(false)
                isVisible = SDK == A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.unlock_recent_files)
                key = "unlock_recent_files"
                setDefaultValue(false)
                isVisible = SDK == A13
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_run_in_background)
                if (osCode >= 37) summary = getString(R.string.enable_run_in_background_summary)
                key = "enable_run_in_background"
                setDefaultValue(false)
                isVisible = osCode >= 27
                isIconSpaceReserved = false
            })
        }
    }
}