package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A15
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class OplusGallery : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.gallery3d", "com.oplus.aiunit")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusGallery

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            if (osCode < 27) return@apply
            //水印
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.GalleryWaterMark)
                key = "GalleryWaterMark"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_watermark_editing)
                key = "enable_watermark_editing"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.replace_oneplus_model_watermark)
                summary = getString(R.string.replace_oneplus_model_watermark_summary)
                key = "replace_oneplus_model_watermark"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_watermark_word_limit)
                key = "remove_gallery_watermark_word_limit"
                setDefaultValue(false)
                isVisible = osCode < 30
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = "设置画框水印状态"
                key = "enable_frame_watermark"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_hassel_watermark)
                key = "enable_hassel_watermark"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_privacy_watermark)
                key = "enable_privacy_watermark"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_spring_festival_watermark)
                summary = getString(R.string.enable_spring_festival_watermark_summary)
                key = "enable_spring_festival_watermark"
                setDefaultValue(false)
                isVisible = osCode >= 27 && isZh(this@loadPreferences)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_national_day_watermark)
                summary = getString(R.string.enable_national_day_watermark_summary)
                key = "enable_national_day_watermark"
                setDefaultValue(false)
                isVisible = osCode >= 27 && isZh(this@loadPreferences)
                isIconSpaceReserved = false
            })
            //滤镜
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.CameraFilter)
                key = "GalleryFilter"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.camera_filter_jiangwen)
                key = "enable_gallery_jiangwen_filter"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //视图
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.GalleryView)
                key = "GalleryView"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_photo_listview_senior_picked)
                key = "enable_photo_listview_senior_picked"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.set_photo_view_thumb_line_display_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_photo_view_thumb_line_display_mode"
                setEntries(R.array.universal_switch_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
            //编辑器
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.GalleryEditor)
                key = "GalleryEditor"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_photo_editor_gif_synthesis)
                key = "enable_photo_editor_gif_synthesis"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_lns_cut_photo)
                key = "enable_lns_cut_photo"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_aigc_elimination_limit)
                summary = getString(R.string.remove_aigc_elimination_limit_summary)
                key = "remove_aigc_elimination_limit"
                setDefaultValue(false)
                isVisible = SDK < A15
                isIconSpaceReserved = false
            })
        }
    }

    override fun callOpenMenu() = requireActivity().openApp(scopes)
}