package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putString

@Obfuscate
class Aod : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui", "com.oplus.aod", "com.oplus.uiengine")
    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val path = FileUtils.getDocumentPath(requireActivity(), it)
                ?: return@registerForActivityResult
            requireActivity().putString(ModulePrefs, "custom_random_text_file", path)
        }
        (activity as MainActivity).restart()
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //息屏相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.AodRelated)
                key = "AodRelated"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_aod_music_whitelist)
                key = "remove_aod_music_whitelist"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_aod_notification_icon_whitelist)
                key = "remove_aod_notification_icon_whitelist"
                setDefaultValue(false)
                isVisible = SDK == A13
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_aod_notification_icon_style)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_aod_notification_icon_style"
                setEntries(R.array.set_aod_notification_icon_style_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_enable_screen_off_music_support)
                summary = getString(R.string.force_enable_screen_off_music_support_summary)
                key = "force_enable_screen_off_music_support"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //随机一言
            if (osCode >= 26) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.AodRandomText)
                    key = "AodRandomText"
                    isIconSpaceReserved = false
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.set_random_text_display_mode)
                    summary = arraySummaryLine(
                        getString(R.string.common_words_current_mode) + ": %s",
                        getString(R.string.set_random_text_display_mode_tips1),
                        getString(R.string.set_random_text_display_mode_tips2)
                    )
                    key = "set_random_text_display_mode"
                    setEntries(R.array.set_random_text_display_mode_entries)
                    entryValues = arrayOf("0", "1", "2")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                when (context.getString(ModulePrefs, "set_random_text_display_mode", "0")) {
                    "1" -> addPreference(Preference(context).apply {
                        title = getString(R.string.custom_random_text_file)
                        key = "custom_random_text_file"
                        val path = context.getString(ModulePrefs, key, "")
                        summary = path.ifBlank { "Null" }
                        isCopyingEnabled = path.isNotBlank()
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            pickFile.launch("text/plain")
                            true
                        }
                    })

                    "2" -> addPreference(EditTextPreference(context).apply {
                        title = getString(R.string.custom_random_text_api)
                        dialogTitle = title
                        key = "custom_random_text_api"
                        setDefaultValue("")
                        setSummaryProvider {
                            EditTextPreference.SimpleSummaryProvider.getInstance()
                                .provideSummary(this)
                        }
                        isIconSpaceReserved = false
                    })
                }
            }
            //字体样式
            if (osCode >= 26) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.AodTypface)
                    key = "AodTypface"
                    isIconSpaceReserved = false
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.set_aod_typeface_mode)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "set_aod_typeface_mode"
                    setEntries(R.array.set_aod_typeface_mode_entries)
                    entryValues = arrayOf("0", "1", "2")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getString(ModulePrefs, "set_aod_typeface_mode") != "0") {
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.apply_aod_clock_typeface)
                        key = "apply_aod_clock_typeface"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                }
            }
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}