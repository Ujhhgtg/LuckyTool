package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.drake.net.utils.scopeLife
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.CommandUtils
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.navigatePage
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class OplusOta : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.ota")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(Preference(context).apply {
                title = getString(R.string.unlock_local_upgrade)
                summary = getString(R.string.unlock_local_upgrade_summary)
                key = "unlock_local_upgrade"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    scopeLife {
                        val command = arrayOf(
                            "settings put global development_settings_enabled 1",
                            "pm clear com.oplus.ota",
                            "settings put global airplane_mode_on 1",
                            "am broadcast --user all -a android.intent.action.AIRPLANE_MODE --ez 'state' 'true'",
                            "am start com.oplus.ota/com.oplus.otaui.activity.EntryActivity"
                        )
                        com.drake.net.utils.withDefault { ShellUtils.fastCmd(*command) }
                    }
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_ota_local_update_verity)
                key = "remove_ota_local_update_verity"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                val verityMode =
                    ShellUtils.fastCmd("${CommandUtils.getprop} ${CommandUtils.otaVerityMode}")
                val vbMetaState =
                    ShellUtils.fastCmd("${CommandUtils.getprop} ${CommandUtils.otaVbmetaState}")
                val status =
                    verityMode == "enforcing" || verityMode == "eio" && vbMetaState == "locked"

                title = getString(R.string.restore_ota_update_verity)
                summary = getString(R.string.restore_ota_update_verity_summary, status.toString())
                key = "restore_ota_update_verity"
                isEnabled = !status
                isChecked = status
                isPersistent = false
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    if (newValue as Boolean) {
                        ShellUtils.fastCmd(
                            "${CommandUtils.resetprop} ${CommandUtils.otaVerityMode} enforcing",
                            "${CommandUtils.resetprop} ${CommandUtils.otaVbmetaState} locked"
                        )
                        (activity as MainActivity).restart()
                    }
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.extract_ota_information)
                summary = getString(R.string.extract_ota_information_summary)
                key = "extract_ota_information"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_oplusOta_to_extractOTAFragment, title)
                    true
                }
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = IntentUtils(requireActivity()).jumpOTA()
}