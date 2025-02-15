package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.setPrefsIconRes

@Obfuscate
class OplusMMS : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.mms")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusMMS

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.android.mms"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary =
                arraySummaryDot(getString(R.string.remove_verification_code_floating_window))
            isVisible = SDK >= A13 && checkPackName(key)
            setOnPreferenceClickListener {
                navigatePage(navigateFragmentId, title)
                true
            }
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_verification_code_floating_window)
                key = "remove_verification_code_floating_window"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
        }
    }
}