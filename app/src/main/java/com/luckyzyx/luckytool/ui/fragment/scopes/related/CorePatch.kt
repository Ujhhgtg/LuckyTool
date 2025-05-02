package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dialogCentered
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class CorePatch : BaseScopePreferenceFeagment() {

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.corePatch

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            title = getString(R.string.corepatch)
            summary = getString(R.string.corepatch_summary,"11-16")
            key = "CorePatch"
            isIconSpaceReserved = false
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.ColorOSCorePatchTip)
                key = "ColorOSCorePatchTip"
                isIconSpaceReserved = false
            })
            add(PreferenceCategory(this@loadPreferences).apply {
                setTitle(R.string.corepatch)
                setSummary(R.string.corepatch_summary)
                key = "CorePatch"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.downgr)
                setSummary(R.string.downgr_summary)
                key = "downgrade"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.authcreak)
                setSummary(R.string.authcreak_summary)
                key = "authcreak"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.digestCreak)
                setSummary(R.string.digestCreak_summary)
                key = "digestCreak"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.UsePreSig)
                setSummary(R.string.UsePreSig_summary)
                key = "UsePreSig"
                setDefaultValue(true)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    if (newValue == true) {
                        MaterialAlertDialogBuilder(this@loadPreferences, dialogCentered).apply {
                            setMessage(R.string.usepresig_warn)
                            setPositiveButton(android.R.string.ok, null)
                            show()
                        }
                    }
                    true
                }
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.shared_user_title)
                setSummary(R.string.shared_user_summary)
                key = "sharedUser"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                setTitle(R.string.disable_verification_agent_title)
                setSummary(R.string.disable_verification_agent_summary)
                key = "disableVerificationAgent"
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
        }
    }
}