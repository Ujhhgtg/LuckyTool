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
import com.luckyzyx.luckytool.utils.fixIconSize
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusWeather : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.weather2")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusWeather

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.weather2"
            setPrefsIconRes(key) { resource, show ->
                icon = fixIconSize(resource)
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.disable_weather_jump_browser),
                getString(R.string.remove_weather_some_page_bottom_ads)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_weather_some_page_bottom_ads)
                key = "remove_weather_some_page_bottom_ads"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.disable_weather_jump_browser)
                key = "disable_weather_jump_browser"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_15_day_weather_expand_list)
                key = "enable_15_day_weather_expand_list"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.restore_rainfall_cloud_map_page)
                key = "restore_rainfall_cloud_map_page"
                setDefaultValue(false)
                isVisible = osCode in 30..34
                isIconSpaceReserved = false
            })
        }
    }
}