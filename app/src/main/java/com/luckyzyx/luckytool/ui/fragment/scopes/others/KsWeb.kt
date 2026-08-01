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

class KsWeb : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("ru.kslabs.ksweb")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.ksWeb

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "ru.kslabs.ksweb"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_pro_license)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_pro_license)
                key = "ksweb_remove_check_license"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}