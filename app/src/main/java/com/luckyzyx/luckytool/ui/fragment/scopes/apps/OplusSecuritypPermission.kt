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
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class OplusSecuritypPermission : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.securitypermission")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusSecuritypPermission

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.securitypermission"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.app_start_dialog_use_old_version),
                getString(R.string.enable_always_allow_app_start_dialog),
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.app_start_dialog_use_old_version)
                key = "app_start_dialog_use_old_version"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_always_allow_app_start_dialog)
                key = "enable_always_allow_app_start_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.auto_unlock_app_ecm_permission_restrict)
                key = "auto_unlock_app_ecm_permission_restrict"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}