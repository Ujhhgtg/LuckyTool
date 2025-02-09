package com.luckyzyx.luckytool.ui.fragment.scopes.others

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class AlphaBackupPro : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.ruet_cse_1503050.ragib.appbackup.pro")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.alphaBackupPro

    override val isHidePage: Boolean = requireActivity().checkPackName(scopes.first())

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_pro_license)
                key = "remove_check_license"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}