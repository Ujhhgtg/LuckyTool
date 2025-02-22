package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import android.content.Intent
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.luckytool.utils.showToast

@Obfuscate
class OplusBrowser : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.heytap.browser")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusBrowser

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.heytap.browser"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_ads_from_download_dialog),
                getString(R.string.remove_ads_at_download_page_bottom),
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.browser_concise_mode)
                isVisible = checkPackName("com.heytap.browser")
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
                        showToast("Error: Please check your browser version!")
                    }
                    true
                }
            })
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.common_words_ads)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_ads_from_download_dialog)
                key = "remove_ads_from_download_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_ads_at_download_page_bottom)
                key = "remove_ads_at_download_page_bottom"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_browser_window_limit_number)
                key = "remove_browser_window_limit_number"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_browser_search_bar_app_promotion)
                key = "remove_browser_search_bar_app_promotion"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //old weeather ads
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_ads_from_weather_page)
                summary = getString(R.string.remove_ads_from_weather_page_summary)
                key = "remove_ads_from_weather_page"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
        }
    }
}