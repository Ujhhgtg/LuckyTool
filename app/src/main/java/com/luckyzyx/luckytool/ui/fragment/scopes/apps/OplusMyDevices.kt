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
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class OplusMyDevices : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.heytap.mydevices")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusMyDevices

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.heytap.mydevices"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.force_enable_feiniu_cloud_nas_option)
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.force_enable_feiniu_cloud_nas_option)
                key = "force_enable_feiniu_cloud_nas_option"
                summary = arraySummaryLine(
                    *arrayOf(
                        "com.heytap.mydevices",
                        "com.heytap.accessory",
                        "com.android.systemui",
                        "com.coloros.gallery3d"
                    ).map { s ->
                        val label = AppUtils(context).getAppLabel(s)
                        val stat = AppUtils(context).getAppMeta(s, "support_fn_nas", "null")
                        "$label: $stat"
                    }.toTypedArray()
                )
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }
}