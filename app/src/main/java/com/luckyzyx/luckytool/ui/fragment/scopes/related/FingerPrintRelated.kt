package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putString

@Obfuscate
class FingerPrintRelated : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.fingerPrintRelated

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val cacheFile = FileUtils.getMSMCacheFile(requireActivity(), it)
            requireActivity().putString(
                ModulePrefs, "replace_fingerprint_icon_path", cacheFile?.path ?: ""
            )
        }
        (activity as MainActivity).restart()
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_fingerprint_icon_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "remove_fingerprint_icon_mode"
                setEntries(R.array.remove_fingerprint_icon_mode_entries)
                entryValues = arrayOf("0", "1", "2", "3")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.replace_fingerprint_icon_switch)
                summary = getString(R.string.replace_fingerprint_icon_switch_summary)
                key = "replace_fingerprint_icon_switch"
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (getBoolean(ModulePrefs, "replace_fingerprint_icon_switch", false)) {
                add(Preference(this@loadPreferences).apply {
                    title = getString(R.string.replace_fingerprint_icon_path)
                    key = "replace_fingerprint_icon_path"
                    val path = getString(ModulePrefs, key, "")
                    if (path.isBlank()) {
                        summary = "Null"
                        isIconSpaceReserved = false
                    } else {
                        icon = BitmapFactory.decodeFile(path)?.toDrawable(resources)
                        summary = path
                        isCopyingEnabled = true
                    }
                    setOnPreferenceClickListener {
                        pickImage.launch("image/*")
                        true
                    }
                })
            }
        }
    }
}