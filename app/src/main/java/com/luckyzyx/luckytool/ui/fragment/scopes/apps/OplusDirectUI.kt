package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.setPrefsIconRes

@Obfuscate
class OplusDirectUI : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.directui", "com.coloros.colordirectservice")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusDirectUI

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.directui"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_app_recommend_card),
            )
            isVisible = checkPackName(key) && checkPackName(
                "com.coloros.colordirectservice"
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_app_recommend_card)
                key = "remove_touch_app_recommend_card"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}