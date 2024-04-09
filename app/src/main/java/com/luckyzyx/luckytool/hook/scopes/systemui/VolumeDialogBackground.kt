package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import com.android.internal.graphics.drawable.BackgroundBlurDrawable
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ColorStateListClass
import com.highcapable.yukihookapi.hook.type.android.DialogInterfaceClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.formatColorAlpha

object VolumeDialogBackground : YukiBaseHooker() {
    override fun onHook() {
        var customAlpha =
            prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
        dataChannel.wait<Int>("custom_volume_dialog_background_transparency") {
            customAlpha = it
        }
        if (customAlpha < 0) return

        //Source VolumeDialogImplEx
        VariousClass(
            "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
            "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14
        ).toClass().apply {
            method { name = "isSurrealQualityOn" }.hook {
                replaceToFalse()
            }
            val isOldVolumnDIalog = hasField { name = "mVerticalRowsLayerDrawable" }
            val hasCaptionLayer = hasField { name = "mVolumeCaptionLayerDrawable" }
            val hasAppAdjustLayer = hasField { name = "mVolumeAppAdjustLayerDrawable" }
            if (isOldVolumnDIalog) method { param(DialogInterfaceClass) }.hook {
                before {
                    if (customAlpha < 0) return@before
                    val value = customAlpha * 25
                    field { name = "mVerticalRowsLayerDrawable" }.get(instance)
                        .cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    field { name = "mVolumeMoreLayerDrawable" }.get(instance).cast<LayerDrawable>()
                        ?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    if (hasCaptionLayer) field { name = "mVolumeCaptionLayerDrawable" }
                        .get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                }
            }
            else method { param(DialogInterfaceClass) }.hook {
                before {
                    if (customAlpha < 0) return@before
                    val value = customAlpha * 25
                    field { name = "mVolumeMoreLayerDrawable" }.get(instance).cast<LayerDrawable>()
                        ?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    if (hasAppAdjustLayer) field { name = "mVolumeAppAdjustLayerDrawable" }
                        .get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    if (hasCaptionLayer) field { name = "mVolumeCaptionLayerDrawable" }
                        .get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                }
            }
            method { name = "addVerticalContainerBg" }.hook {
                before {
                    if (customAlpha < 0) return@before
                    val value = customAlpha * 25
                    val mVerticalRowsLayerDrawableMap =
                        field { name = "mVerticalRowsLayerDrawableMap" }.get(instance)
                            .cast<HashMap<Int, LayerDrawable>>() ?: return@before
                    mVerticalRowsLayerDrawableMap.forEach { (_, layer) ->
                        layer.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    }
                }
            }
        }

        //Source OplusVolumeSeekBar
        "com.oplus.systemui.volume.OplusVolumeSeekBar".toClassOrNull()?.apply {
            method { name = "init" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val seekBar = instance<Any>()
                    seekBar.current().method {
                        name = "setProgressColor"
                        param(ColorStateListClass)
                        superClass()
                    }.call(
                        ColorStateList.valueOf(
                            formatColorAlpha(Color.WHITE, 0.5F)
                        )
                    )
                }
            }
        }
    }
}