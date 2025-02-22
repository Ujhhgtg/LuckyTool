package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.drake.net.utils.scopeLife
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.CommandUtils
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class OplusOTA : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.ota")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusOta

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.ota"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.unlock_local_upgrade),
                getString(R.string.restore_ota_update_verity)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(Preference(this@loadPreferences).apply {
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
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_ota_local_update_verity)
                key = "remove_ota_local_update_verity"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.disable_dm_verity_verification)
                key = "disable_dm_verity_verification"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
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
                isVisible = false
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
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.extract_ota_information)
                summary = getString(R.string.extract_ota_information_summary)
                key = "extract_ota_information"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    findNavController().navigatePage(R.id.extractOTAFragment, title)
                    true
                }
            })
        }
    }

    override fun callOpenMenu() = IntentUtils(requireActivity()).jumpOTA()
}