package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.openApp

@Obfuscate
class OplusGallery : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.coloros.gallery3d", "com.oplus.aiunit")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //水印
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.GalleryWaterMark)
                key = "GalleryWaterMark"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_watermark_editing)
                key = "enable_watermark_editing"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.replace_oneplus_model_watermark)
                summary = getString(R.string.replace_oneplus_model_watermark_summary)
                key = "replace_oneplus_model_watermark"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_watermark_word_limit)
                key = "remove_gallery_watermark_word_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_spring_festival_watermark)
                summary = getString(R.string.enable_spring_festival_watermark_summary)
                key = "enable_spring_festival_watermark"
                setDefaultValue(false)
                isVisible = osCode >= 27 && isZh(context)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_national_day_watermark)
                summary = getString(R.string.enable_national_day_watermark_summary)
                key = "enable_national_day_watermark"
                setDefaultValue(false)
                isVisible = osCode >= 27 && isZh(context)
                isIconSpaceReserved = false
            })
            //滤镜
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.CameraFilter)
                key = "GalleryFilter"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.camera_filter_jiangwen)
                key = "enable_gallery_jiangwen_filter"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //视图
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.GalleryView)
                key = "GalleryView"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_photo_listview_senior_picked)
                key = "enable_photo_listview_senior_picked"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_photo_view_thumb_line_display_mode)
                summary = getString(R.string.common_words_current_mode) + ": %s"
                key = "set_photo_view_thumb_line_display_mode"
                setEntries(R.array.universal_switch_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isIconSpaceReserved = false
            })
            //编辑器
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.GalleryEditor)
                key = "GalleryEditor"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_photo_editor_gif_synthesis)
                key = "enable_photo_editor_gif_synthesis"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_lns_cut_photo)
                key = "enable_lns_cut_photo"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_aigc_elimination_limit)
                summary = getString(R.string.remove_aigc_elimination_limit_summary)
                key = "remove_aigc_elimination_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = requireActivity().openApp(scopes)
}