package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import android.os.Build
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
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.AppInfoSelector
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.CameraUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.openApp
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.luckytool.utils.setSummaryProvider
import com.luckyzyx.luckytool.utils.showToast

@Obfuscate
class OplusCamera : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.camera", "com.oneplus.camera")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusCamera

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            val isOneplusCamera = checkPackName("com.oneplus.camera")
            key = if (isOneplusCamera) "com.oneplus.camera" else "com.oplus.camera"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_watermark_word_limit),
                getString(R.string.enable_10_bit_image_support)
            )
            isVisible = checkPackName(key)
            setOnPreferenceClickListener {
                navigatePage(navigateFragmentId, title)
                true
            }
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_camera_debug_ui_option)
                key = "enable_camera_debug_ui_option"
                setDefaultValue(false)
                isVisible = osCode >= 30
                isIconSpaceReserved = false
            })
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.custom_camera_open_gallery_by_default)
                key = "custom_camera_open_gallery_by_default"
                summary = arraySummaryLine(getString(ModulePrefs, key, "").ifBlank {
                    getString(R.string.common_words_not_set)
                })
                isVisible = osCode >= 26
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    AppInfoSelector(this@loadPreferences, true).apply {
                        setOnSelectAppListener(object : OnSelectAppInfoListener {
                            override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                if (list.size > 1) {
                                    showToast(getString(R.string.custom_camera_open_gallery_by_default_tips))
                                    return
                                }
                                val packName = list.firstOrNull()?.packageName ?: ""
                                putString(ModulePrefs, key, packName)
                                (activity as MainActivity).restart()
                            }
                        })
                        show()
                    }
                    true
                }
            })
            if (osCode >= 28) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_camera_night_zoom_30x)
                    key = "enable_camera_night_zoom_30x"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_video_capture_roulette_zoom)
                    key = "enable_video_capture_roulette_zoom"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_camera_flash_limit)
                key = "remove_camera_flash_limit"
                setDefaultValue(false)
                isVisible = osCode >= 26
                isIconSpaceReserved = false
            })
            //水印
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.CameraWaterMark)
                key = "CameraWaterMark"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_watermark_word_limit)
                key = "remove_watermark_word_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            if (SDK >= A13) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_frame_watermark_style)
                    key = "enable_frame_watermark_style"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) findPreference<SwitchPreference>(
                            "enable_hasselblad_watermark_style"
                        )?.isChecked = false
                        true
                    }
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_hasselblad_watermark_style)
                    key = "enable_hasselblad_watermark_style"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) findPreference<SwitchPreference>(
                            "enable_frame_watermark_style"
                        )?.isChecked = false
                        true
                    }
                })
                add(EditTextPreference(this@loadPreferences).apply {
                    title = getString(R.string.custom_model_watermark)
                    dialogTitle = title
                    key = "custom_model_watermark"
                    setDefaultValue("None")
                    setSummaryProvider(this)
                    isIconSpaceReserved = false
                    isVisible = Build.FINGERPRINT.contains("RMX", true).not()
                })
            }
            //滤镜
            if (SDK >= A13) {
                add(PreferenceCategory(this@loadPreferences).apply {
                    title = getString(R.string.CameraFilter)
                    key = "CameraFilter"
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.remove_filter_model_limit)
                    key = "remove_filter_model_limit"
                    setDefaultValue(false)
                    isVisible = osCode >= 34
                    isIconSpaceReserved = false
                })
                add(Preference(this@loadPreferences).apply {
                    val defaultFilters = CameraUtils.getCameraFilters(this@loadPreferences)
                    title = getString(R.string.camera_universal_filter_settings)
                    key = "camera_universal_filter_settings"
                    getStringSet(ModulePrefs, key, ArraySet()).forEach {
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
                        MaterialAlertDialogBuilder(this@loadPreferences, dialogCentered).apply {
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
                                putStringSet(ModulePrefs, key, set.toSet())
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
                add(Preference(this@loadPreferences).apply {
                    val defaultFilters = CameraUtils.getPortraitCameraFilters(this@loadPreferences)
                    title = getString(R.string.camera_portrait_filter_settings)
                    key = "camera_portrait_filter_settings"
                    getStringSet(ModulePrefs, key, ArraySet()).forEach {
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
                        MaterialAlertDialogBuilder(this@loadPreferences, dialogCentered).apply {
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
                                putStringSet(ModulePrefs, key, set.toSet())
                                (activity as MainActivity).restart()
                            }
                            setNeutralButton(android.R.string.cancel, null)
                        }.show()
                        true
                    }
                })
                add(Preference(this@loadPreferences).apply {
                    val defaultFilters = CameraUtils.getVideoCameraFilters(this@loadPreferences)
                    title = getString(R.string.camera_video_filter_settings)
                    key = "camera_video_filter_settings"
                    getStringSet(ModulePrefs, key, ArraySet()).forEach {
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
                        MaterialAlertDialogBuilder(this@loadPreferences, dialogCentered).apply {
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
                                putStringSet(ModulePrefs, key, set.toSet())
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
                add(PreferenceCategory(this@loadPreferences).apply {
                    title = getString(R.string.settings_other_preference)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_10_bit_image_support)
                    summary = getString(R.string.enable_10_bit_image_support_summary)
                    key = "enable_10_bit_image_support"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}