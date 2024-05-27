package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.showToast

@Obfuscate
class OplusBrowser : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.heytap.browser")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(Preference(context).apply {
                title = getString(R.string.browser_concise_mode)
                isVisible = context.checkPackName("com.heytap.browser")
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    try {
                        Intent().apply {
                            setClassName(
                                "com.heytap.browser",
                                "com.heytap.browser.settings.component.BrowserPreferenceActivity"
                            )
                            putExtra(
                                "key.fragment.name",
                                "com.heytap.browser.settings.homepage.HomepagePreferenceFragment"
                            )
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                            startActivity(this)
                        }
                    } catch (_: Exception) {
                        context.showToast("Error: Please check your browser version!")
                    }
                    true
                }
            })
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.common_words_ads)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_ads_from_download_dialog)
                key = "remove_ads_from_download_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_ads_at_download_page_bottom)
                key = "remove_ads_at_download_page_bottom"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_browser_window_limit_number)
                key = "remove_browser_window_limit_number"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //old weeather ads
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_ads_from_weather_page)
                summary = getString(R.string.remove_ads_from_weather_page_summary)
                key = "remove_ads_from_weather_page"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}