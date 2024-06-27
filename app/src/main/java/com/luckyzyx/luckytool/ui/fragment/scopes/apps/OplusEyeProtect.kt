package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class OplusEyeProtect : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.eyeprotect")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_eyeprotect_paper_texture_support)
                summary = getString(R.string.need_restart_system)
                key = "enable_eyeprotect_paper_texture_support"
                setDefaultValue(false)
                isVisible = osCode >= 33
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = requireActivity().openApp(scopes)
}