package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class OplusNfc : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.nfc")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusNfc

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.android.nfc"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.scan_nfc_tag_auto_click)
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.scan_nfc_tag_auto_click)
                key = "scan_nfc_tag_auto_click"
                summary = getString(R.string.need_restart_system)
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}