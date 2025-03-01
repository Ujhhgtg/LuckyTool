package com.luckyzyx.luckytool.ui.fragment.scopes.others

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.openApp
import com.luckyzyx.luckytool.utils.setPrefsIconRes

@Obfuscate
class ADM : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.dv.adm")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.adm

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.dv.adm"
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
                key = "adm_unlock_pro"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.adm_unlock_more_threads)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "adm_unlock_more_threads"
                setEntries(R.array.adm_unlock_more_threads_entries)
                entryValues = arrayOf("0", "32", "64", "128")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}