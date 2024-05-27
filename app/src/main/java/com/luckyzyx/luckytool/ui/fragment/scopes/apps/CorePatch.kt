package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dialogCentered

@Obfuscate
class CorePatch : BaseScopePreferenceFeagment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(Preference(context).apply {
                title = getString(R.string.ColorOSCorePatchTip)
                key = "ColorOSCorePatchTip"
                isIconSpaceReserved = false
            })
            addPreference(PreferenceCategory(context).apply {
                setTitle(R.string.corepatch)
                setSummary(R.string.corepatch_summary)
                key = "CorePatch"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.downgr)
                setSummary(R.string.downgr_summary)
                key = "downgrade"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.authcreak)
                setSummary(R.string.authcreak_summary)
                key = "authcreak"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.digestCreak)
                setSummary(R.string.digestCreak_summary)
                key = "digestCreak"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.UsePreSig)
                setSummary(R.string.UsePreSig_summary)
                key = "UsePreSig"
                setDefaultValue(true)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    if (newValue == true) {
                        MaterialAlertDialogBuilder(context, dialogCentered).apply {
                            setMessage(R.string.usepresig_warn)
                            setPositiveButton(android.R.string.ok, null)
                            show()
                        }
                    }
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.enhancedMode)
                setSummary(R.string.enhancedMode_summary)
                key = "enhancedMode"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                setTitle(R.string.shared_user_title)
                setSummary(R.string.shared_user_summary)
                key = "sharedUser"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}