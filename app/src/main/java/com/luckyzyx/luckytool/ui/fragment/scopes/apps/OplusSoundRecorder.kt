package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusSoundRecorder : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.coloros.soundrecorder",
        "com.oplus.audiomonitor",
        "com.oplus.atlas",
        "com.oplus.audio.effectcenter"
    )

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusSoundRecorder

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.soundrecorder"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.enable_record_calls_on_third_party_apps)
            )
            isVisible = osCode >= 30 && checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_record_calls_on_third_party_apps)
                summary = arraySummaryLine(
                    getString(R.string.need_restart_system),
                    getString(R.string.enable_record_calls_on_third_party_apps_tips),
                    getString(R.string.enable_record_calls_on_third_party_apps_tips_2)
                )
                key = "enable_record_calls_on_third_party_apps"
                setDefaultValue(false)
                isEnabled =
                    checkPackName("com.oplus.audiomonitor") && checkPackName("com.oplus.atlas")
                isVisible = osCode == 30 && isZh(context)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.expand_voip_recorder_whitelist)
                summary = "企业微信,TIM,飞书,抖音"
                key = "expand_voip_recorder_whitelist"
                setDefaultValue(false)
                isEnabled = checkPackName("com.oplus.audiomonitor")
                isVisible = osCode in 31..33 && isZh(context)
                isIconSpaceReserved = false
            })
        }
    }
}