package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
class Screenshot : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.screenshot", "com.oplus.appplatform")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_system_screenshot_delay)
                summary = getString(R.string.remove_system_screenshot_delay_summary)
                key = "remove_system_screenshot_delay"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_screenshot_privacy_limit)
                summary = getString(R.string.remove_screenshot_privacy_limit_summary)
                key = "remove_screenshot_privacy_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.disable_flag_secure)
                summary = getString(R.string.disable_flag_secure_summary)
                key = "disable_flag_secure"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_page_limit_for_long_screenshots)
                summary = getString(R.string.remove_page_limit_for_long_screenshots_summary)
                key = "remove_page_limit_for_long_screenshots"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_png_save_format)
                key = "enable_png_save_format"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}