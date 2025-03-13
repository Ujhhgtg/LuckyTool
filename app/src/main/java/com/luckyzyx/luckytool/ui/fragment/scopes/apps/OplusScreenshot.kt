package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class OplusScreenshot : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.screenshot", "com.oplus.appplatform")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusScreenshot

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.screenshot"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_system_screenshot_delay),
                getString(R.string.remove_screenshot_privacy_limit)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_system_screenshot_delay)
                summary = getString(R.string.remove_system_screenshot_delay_summary)
                key = "remove_system_screenshot_delay"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_screenshot_privacy_limit)
                summary = arraySummaryLine(
                    getString(R.string.remove_screenshot_privacy_limit_summary),
                    getString(R.string.need_restart_system)
                )
                key = "remove_screenshot_privacy_limit"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.disable_flag_secure)
                summary = arraySummaryLine(
                    getString(R.string.disable_flag_secure_summary),
                    getString(R.string.need_restart_system)
                )
                key = "disable_flag_secure"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_page_limit_for_long_screenshots)
                summary = getString(R.string.remove_page_limit_for_long_screenshots_summary)
                key = "remove_page_limit_for_long_screenshots"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_png_save_format)
                key = "enable_png_save_format"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}