package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Build
import android.os.Bundle
import android.util.ArraySet
import androidx.appcompat.app.AlertDialog
import androidx.core.util.forEach
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.CameraFilter
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.AppInfoSelector
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.openApp
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.showToast

@Obfuscate
class OplusCamera : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.camera", "com.oneplus.camera")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_camera_debug_ui_option)
                key = "enable_camera_debug_ui_option"
                setDefaultValue(false)
                isVisible = osCode >= 30
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.custom_camera_open_gallery_by_default)
                key = "custom_camera_open_gallery_by_default"
                summary = arraySummaryLine(context.getString(ModulePrefs, key, "").ifBlank {
                    getString(R.string.common_words_not_set)
                })
                isVisible = osCode >= 26
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    AppInfoSelector(context, true).apply {
                        setOnSelectAppListener(object : OnSelectAppInfoListener {
                            override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                if (list.size > 1) {
                                    context.showToast(getString(R.string.custom_camera_open_gallery_by_default_tips))
                                    return
                                }
                                val packName = list.firstOrNull()?.packageName ?: ""
                                context.putString(ModulePrefs, key, packName)
                                (activity as MainActivity).restart()
                            }
                        })
                        show()
                    }
                    true
                }
            })
            if (osCode >= 28) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_camera_night_zoom_30x)
                    key = "enable_camera_night_zoom_30x"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_video_capture_roulette_zoom)
                    key = "enable_video_capture_roulette_zoom"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            //水印
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.CameraWaterMark)
                key = "CameraWaterMark"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_watermark_word_limit)
                key = "remove_watermark_word_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            if (SDK >= A13) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_frame_watermark_style)
                    key = "enable_frame_watermark_style"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_hasselblad_watermark_style)
                    key = "enable_hasselblad_watermark_style"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(EditTextPreference(context).apply {
                    title = getString(R.string.custom_model_watermark)
                    dialogTitle = title
                    key = "custom_model_watermark"
                    setDefaultValue("None")
                    setSummaryProvider {
                        EditTextPreference.SimpleSummaryProvider.getInstance().provideSummary(this)
                    }
                    isIconSpaceReserved = false
                    isVisible = Build.FINGERPRINT.contains("RMX", true).not()
                })
            }
            //滤镜
            if (SDK >= A13) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.CameraFilter)
                    key = "CameraFilter"
                    isIconSpaceReserved = false
                })
                addPreference(Preference(context).apply {
                    val defaultFilters = ArrayList<CameraFilter>().apply {
                        add(CameraFilter("master_filter", getString(R.string.camera_filter_master)))
                        add(
                            CameraFilter(
                                "jiangwen_filter", getString(R.string.camera_filter_jiangwen)
                            )
                        )
                        add(
                            CameraFilter(
                                "grand_tour_filter", getString(R.string.camera_filter_grand_tour)
                            )
                        )
                    }
                    title = getString(R.string.camera_universal_filter_settings)
                    key = "camera_universal_filter_settings"
                    context.getStringSet(ModulePrefs, key, ArraySet()).forEach {
                        defaultFilters.find { its -> its.key == it }?.isEnable = true
                    }
                    val keys = ArrayList<String>()
                    val titles = ArrayList<String>()
                    val values = ArrayList<Boolean>()
                    val enabledTitle = ArrayList<String>()
                    defaultFilters.forEachIndexed { _, filter ->
                        keys.add(filter.key)
                        titles.add(filter.title)
                        values.add(filter.isEnable)
                        if (filter.isEnable) enabledTitle.add(filter.title)
                    }
                    summary = enabledTitle.toString()
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        MaterialAlertDialogBuilder(context, dialogCentered).apply {
                            setTitle(title)
                            setMultiChoiceItems(
                                titles.toTypedArray(), values.toBooleanArray(), null
                            )
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                val positions =
                                    (dialog as AlertDialog).listView.checkedItemPositions
                                val set = ArraySet<String>()
                                positions.forEach { position, isChecked ->
                                    val key = keys[position]
                                    if (isChecked) set.add(key)
                                }
                                context.putStringSet(ModulePrefs, key, set.toSet())
                                if (set.contains("master_filter")) findPreference<SwitchPreference>(
                                    "enable_hasselblad_watermark_style"
                                )?.isChecked = true
                                (activity as MainActivity).restart()
                            }
                            setNeutralButton(android.R.string.cancel, null)
                        }.show()
                        true
                    }
                })
                addPreference(Preference(context).apply {
                    val defaultFilters = ArrayList<CameraFilter>().apply {
                        add(CameraFilter("retention", getString(R.string.camera_filter_retention)))
                        add(
                            CameraFilter(
                                "bokeh_flare_portrait",
                                getString(R.string.camera_filter_bokeh_flare_portrait)
                            )
                        )
                    }
                    title = getString(R.string.camera_portrait_filter_settings)
                    key = "camera_portrait_filter_settings"
                    context.getStringSet(ModulePrefs, key, ArraySet()).forEach {
                        defaultFilters.find { its -> its.key == it }?.isEnable = true
                    }
                    val keys = ArrayList<String>()
                    val titles = ArrayList<String>()
                    val values = ArrayList<Boolean>()
                    val enabledTitle = ArrayList<String>()
                    defaultFilters.forEachIndexed { _, filter ->
                        keys.add(filter.key)
                        titles.add(filter.title)
                        values.add(filter.isEnable)
                        if (filter.isEnable) enabledTitle.add(filter.title)
                    }
                    summary = enabledTitle.toString()
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        MaterialAlertDialogBuilder(context, dialogCentered).apply {
                            setTitle(title)
                            setMultiChoiceItems(
                                titles.toTypedArray(), values.toBooleanArray(), null
                            )
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                val positions =
                                    (dialog as AlertDialog).listView.checkedItemPositions
                                val set = ArraySet<String>()
                                positions.forEach { position, isChecked ->
                                    val key = keys[position]
                                    if (isChecked) set.add(key)
                                }
                                context.putStringSet(ModulePrefs, key, set.toSet())
                                (activity as MainActivity).restart()
                            }
                            setNeutralButton(android.R.string.cancel, null)
                        }.show()
                        true
                    }
                })
                addPreference(Preference(context).apply {
                    val defaultFilters = ArrayList<CameraFilter>().apply {
                        add(
                            CameraFilter(
                                "color_extraction",
                                getString(R.string.camera_filter_color_extraction)
                            )
                        )
                        add(CameraFilter("retention", getString(R.string.camera_filter_retention)))
                        add(
                            CameraFilter(
                                "bokeh_flare_portrait",
                                getString(R.string.camera_filter_bokeh_flare_portrait)
                            )
                        )
                    }
                    title = getString(R.string.camera_video_filter_settings)
                    key = "camera_video_filter_settings"
                    context.getStringSet(ModulePrefs, key, ArraySet()).forEach {
                        defaultFilters.find { its -> its.key == it }?.isEnable = true
                    }
                    val keys = ArrayList<String>()
                    val titles = ArrayList<String>()
                    val values = ArrayList<Boolean>()
                    val enabledTitle = ArrayList<String>()
                    defaultFilters.forEachIndexed { _, filter ->
                        keys.add(filter.key)
                        titles.add(filter.title)
                        values.add(filter.isEnable)
                        if (filter.isEnable) enabledTitle.add(filter.title)
                    }
                    summary = enabledTitle.toString()
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        MaterialAlertDialogBuilder(context, dialogCentered).apply {
                            setTitle(title)
                            setMultiChoiceItems(
                                titles.toTypedArray(), values.toBooleanArray(), null
                            )
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                val positions =
                                    (dialog as AlertDialog).listView.checkedItemPositions
                                val set = ArraySet<String>()
                                positions.forEach { position, isChecked ->
                                    val key = keys[position]
                                    if (isChecked) set.add(key)
                                }
                                context.putStringSet(ModulePrefs, key, set.toSet())
                                (activity as MainActivity).restart()
                            }
                            setNeutralButton(android.R.string.cancel, null)
                        }.show()
                        true
                    }
                })
            }
            //其他
            if (SDK >= A13) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.settings_other_preference)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_10_bit_image_support)
                    summary = getString(R.string.enable_10_bit_image_support_summary)
                    key = "enable_10_bit_image_support"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = requireActivity().openApp(scopes)
}