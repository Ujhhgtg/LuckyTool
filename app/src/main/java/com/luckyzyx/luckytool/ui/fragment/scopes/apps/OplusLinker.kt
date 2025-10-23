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
class OplusLinker : BaseScopePreferenceFeagment() {
    override val scopes =
        arrayOf("com.oplus.linker", "com.android.contacts", "com.android.bluetooth")

    override val isEnableRestartMenu: Boolean = false

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusLinker

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.linker"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.force_enable_iphone_shared_support)
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_enable_iphone_shared_support)
                summary = getString(R.string.need_restart_system)
                key = "force_enable_iphone_shared_support"
                setDefaultValue(false)
                isVisible = osCode >= 37
                isIconSpaceReserved = false
            })
        }
    }
}