package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusPermissionController : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.permissioncontroller")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusPermissionController

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.android.permissioncontroller"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.unlock_default_desktop_limit),
                getString(R.string.remove_storage_permission_exception_dialog)
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.unlock_default_desktop_limit)
                key = "unlock_default_desktop_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_storage_permission_exception_dialog)
                key = "remove_storage_permission_exception_dialog"
                setDefaultValue(false)
                isVisible = osCode < 37
                isIconSpaceReserved = false
            })
        }
    }
}