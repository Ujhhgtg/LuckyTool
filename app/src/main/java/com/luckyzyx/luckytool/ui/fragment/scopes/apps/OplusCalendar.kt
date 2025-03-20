package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes

@Obfuscate
class OplusCalendar : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.calendar")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusCalendar

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.calendar"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryLine(
                getString(R.string.remove_holiday_page_information_flow),
                getString(R.string.remove_almanac_page_information_flow)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_holiday_page_information_flow)
                key = "remove_holiday_page_information_flow"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_almanac_page_information_flow)
                key = "remove_almanac_page_information_flow"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_horoscope_page_information_flow)
                key = "remove_horoscope_page_information_flow"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}