package com.luckyzyx.luckytool.ui.fragment.scopes.others

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class ADM : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.dv.adm")
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.adm_unlock_pro)
                key = "adm_unlock_pro"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.adm_unlock_more_threads)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "adm_unlock_more_threads"
                setEntries(R.array.adm_unlock_more_threads_entries)
                entryValues = arrayOf("0", "32", "64","128")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = requireActivity().openApp(scopes)
}