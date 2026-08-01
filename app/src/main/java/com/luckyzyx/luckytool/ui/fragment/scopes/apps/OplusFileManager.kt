package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.setPrefsIconRes

class OplusFileManager : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.filemanager")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = false

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusFileManager

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.coloros.filemanager"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_word_limit_for_saving_files)
            )
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_word_limit_for_saving_files)
                key = "remove_word_limit_for_saving_files"
                setDefaultValue(false)
                isVisible = osCode >= 37
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_word_limit_for_compress_files)
                key = "remove_word_limit_for_compress_files"
                setDefaultValue(false)
                isVisible = osCode >= 37
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_word_limit_for_label_name_files)
                key = "remove_word_limit_for_label_name_files"
                setDefaultValue(false)
                isVisible = osCode >= 37
                isIconSpaceReserved = false
            })
        }
    }
}