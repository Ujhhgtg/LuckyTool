package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.isZh

@Obfuscate
class OplusSoundRecorder : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.coloros.soundrecorder",
        "com.oplus.audiomonitor",
        "com.oplus.atlas",
        "com.oplus.audio.effectcenter"
    )

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_record_calls_on_third_party_apps)
                summary = arraySummaryLine(
                    getString(R.string.need_restart_system),
                    getString(R.string.enable_record_calls_on_third_party_apps_tips),
                    getString(R.string.enable_record_calls_on_third_party_apps_tips_2)
                )
                key = "enable_record_calls_on_third_party_apps"
                setDefaultValue(false)
                isEnabled =
                    context.checkPackName("com.oplus.audiomonitor") && context.checkPackName("com.oplus.atlas")
                isVisible = osCode >= 30 && isZh(context)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.expand_voip_recorder_whitelist)
                summary = "企业微信,TIM,飞书,抖音"
                key = "expand_voip_recorder_whitelist"
                setDefaultValue(false)
                isEnabled = context.checkPackName("com.oplus.audiomonitor")
                isVisible = osCode == 31 && isZh(context)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}