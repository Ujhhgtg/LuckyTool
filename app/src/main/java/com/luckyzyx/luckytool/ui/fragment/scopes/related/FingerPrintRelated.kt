package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.contract.CropImageContract
import com.luckyzyx.luckytool.data.CropImageContractOptions
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.getUri
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.showToast

@Obfuscate
class FingerPrintRelated : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.fingerPrintRelated

    private val cropImage = registerForActivityResult(CropImageContract()) {
        if (it.isSuccessful) {
            val uri = it.uriContent
            if (uri == null || uri == Uri.EMPTY) return@registerForActivityResult
            val path = uri.path ?: ""
            if (path.isNotBlank()) {
                requireActivity().showToast(path)
                requireActivity().putString(
                    ModulePrefs, "replace_fingerprint_icon_path", path
                )
                (activity as MainActivity).restart()
            }
        } else {
            LogUtils.e("CropImage", "error", it.error.toString(), true)
        }
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
                        val cacheImageFile = FileUtils.createCacheFile(requireActivity(), "png")
                        cropImage.launch(
                            CropImageContractOptions(
                                null, CropImageOptions().apply {
                                    activityTitle = title?.toString() ?: ""
                                    cropShape = CropImageView.CropShape.RECTANGLE
                                    guidelines = CropImageView.Guidelines.ON_TOUCH
                                    aspectRatioX = 216
                                    aspectRatioY = 216
                                    maxCropResultWidth = 216
                                    maxCropResultHeight = 216
                                    fixAspectRatio = true
                                    customOutputUri = cacheImageFile.getUri
                                    outputCompressFormat = Bitmap.CompressFormat.PNG
                                    outputCompressQuality = 100
                                }
                            )
                        )
                        true
                    }
                })
            }
        }
    }
}