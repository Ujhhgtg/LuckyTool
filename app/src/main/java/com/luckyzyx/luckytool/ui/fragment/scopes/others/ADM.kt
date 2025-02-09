package com.luckyzyx.luckytool.ui.fragment.scopes.others

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class ADM : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.dv.adm")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.adm

    override val isHidePage: Boolean = requireActivity().checkPackName(scopes.first())

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