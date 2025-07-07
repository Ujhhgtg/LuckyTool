package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.android.internal.graphics.drawable.BackgroundBlurDrawable
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.formatColorAlpha
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
class VolumeDialogBackground(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 35) loadHooker(VolumeDialog(dexKitBridge))
        else loadHooker(VolumeDialogV14)
    }

    @Obfuscate
    class VolumeDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            var customAlpha =
                prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
            dataChannel.wait<Int>("custom_volume_dialog_background_transparency") {
                customAlpha = it
            }
            if (customAlpha < 0) return

            //Source VolumeDialogImplEx
            val volumnDialogClazz = VariousClass(
                "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
                "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14 C15
            ).toClass() as Class<Any>

            volumnDialogClazz.resolve().apply {
                firstMethodOrNull { name = "isSurrealQualityOn" }?.hook {
                    replaceToFalse()
                }

                dexKitBridge.findClass {
                    matcher {
                        className(volumnDialogClazz.name, StringMatchType.StartsWith)
                        addMethod {
                            paramTypes(DialogInterface::class.java)
                            usingStrings("initDialog")
                            usingNumbers(0, -1)
                        }
                    }
                }.apply {
                    checkDataList("VolumeDialogBackground find onShow")
                    single().name.toClass().resolve().apply {
                        firstMethod { parameters(DialogInterface::class) }.hook {
                            before {
                                if (customAlpha < 0) return@before
                                val value = customAlpha * 25

                                val ins = firstFieldOrNull { type = volumnDialogClazz }?.let {
                                    it.of(instance).get() ?: return@before
                                } ?: instance

                                ins.asResolver().firstFieldOrNull {
                                    name = "mVerticalRowsLayerDrawable"
                                }?.get<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                ins.asResolver().firstFieldOrNull {
                                    name = "mVolumeMoreLayerDrawable"
                                }?.get<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                ins.asResolver().firstFieldOrNull {
                                    name = "mVolumeAppAdjustLayerDrawable"
                                }?.get<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                ins.asResolver().firstFieldOrNull {
                                    name = "mVolumeCaptionLayerDrawable"
                                }?.get<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                ins.asResolver().firstFieldOrNull {
                                    name = "mVolumeBackgroundLayerDrawable"
                                }?.get<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                ins.asResolver().firstFieldOrNull {
                                    name = "mVolumeBtnDrawable"
                                }?.get<Drawable>()?.apply {
                                    alpha = 255 - value
                                }
                            }
                        }
                    }
                }
                firstFieldOrNull { name = "mVerticalRowsLayerDrawableMap" }?.let {
                    firstMethodOrNull { name = "updateRowsH" }?.hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            it.copy().of(instance).get<HashMap<Int, LayerDrawable>>()?.apply {
                                forEach { (key, layer) ->
                                    val blurDrawable = layer.getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    layer.getDrawable(1)?.alpha = value
                                    put(key, layer)
                                }
                            }
                        }
                    }
                }
                firstMethodOrNull { name = "expandPanel" }?.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        firstFieldOrNull {
                            name = "mVolumeBackgroundLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    }
                }
            }

            //Source OplusVolumeSeekBar
            "com.oplus.systemui.volume.OplusVolumeSeekBar".toClassOrNull()?.resolve()?.apply {
                constructor {}.hookAll {
                    after {
                        if (customAlpha < 0) return@after
                        val seekBar = instance<Any>()
                        seekBar.asResolver().firstMethod {
                            name = "setProgressColor"
                            parameters(ColorStateList::class)
                            superclass()
                        }.invoke(
                            ColorStateList.valueOf(
                                formatColorAlpha(Color.WHITE, 0.5F)
                            )
                        )
                    }
                }
            }

            //Source VolumeBlurManager
            "com.oplus.systemui.volume.utils.VolumeBlurManager".toClassOrNull()?.resolve()?.apply {
                firstMethod { name = "getBackgroundBlurDrawable" }.hook {
                    after {
                        if (customAlpha < 0) return@after
                        val value = customAlpha * 25
                        val drawable = result<Drawable>()
                        if (drawable is BackgroundBlurDrawable) {
                            drawable.setBlurRadius(value.dp)
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    object VolumeDialogV14 : YukiBaseHooker() {
        override fun onHook() {
            var customAlpha =
                prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
            dataChannel.wait<Int>("custom_volume_dialog_background_transparency") {
                customAlpha = it
            }
            if (customAlpha < 0) return

            //Source VolumeDialogImplEx
            val volumnDialogClazz = VariousClass(
                "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
                "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14 C15
            ).toClass() as Class<Any>

            volumnDialogClazz.resolve().apply {
                firstMethodOrNull { name = "isSurrealQualityOn" }?.hook {
                    replaceToFalse()
                }

                firstMethod { parameters(DialogInterface::class) }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        firstFieldOrNull {
                            name = "mVerticalRowsLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        firstFieldOrNull {
                            name = "mVolumeMoreLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        firstFieldOrNull {
                            name = "mVolumeAppAdjustLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        firstFieldOrNull {
                            name = "mVolumeCaptionLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        firstFieldOrNull {
                            name = "mVolumeBackgroundLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        firstFieldOrNull {
                            name = "mVolumeBtnDrawable"
                        }?.of(instance)?.get<Drawable>()?.apply {
                            alpha = 255 - value
                        }
                    }
                }
                firstFieldOrNull { name = "mVerticalRowsLayerDrawableMap" }?.let {
                    (firstMethodOrNull { name = "addVerticalContainerBg" }
                        ?: firstMethod { name = "updateVolumeRowBgForSide" }).hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            it.copy().of(instance).get<HashMap<Int, LayerDrawable>>()?.apply {
                                forEach { (key, layer) ->
                                    val blurDrawable = layer.getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    layer.getDrawable(1)?.alpha = value
                                    put(key, layer)
                                }
                            }
                        }
                    }
                }
                firstFieldOrNull { name = "mVerticalRowsLayerDrawableMap" }?.let {
                    firstMethodOrNull { name = "updateRowsH" }?.hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            it.copy().of(instance).get<HashMap<Int, LayerDrawable>>()?.apply {
                                forEach { (key, layer) ->
                                    val blurDrawable = layer.getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    layer.getDrawable(1)?.alpha = value
                                    put(key, layer)
                                }
                            }
                        }
                    }
                }
                firstMethodOrNull { name = "expandPanel" }?.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        firstFieldOrNull {
                            name = "mVolumeBackgroundLayerDrawable"
                        }?.of(instance)?.get<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                    }
                }
            }

            //Source OplusVolumeSeekBar
            "com.oplus.systemui.volume.OplusVolumeSeekBar".toClassOrNull()?.resolve()?.apply {
                firstMethod { name = "init" }.hook {
                    after {
                        if (customAlpha < 0) return@after
                        val seekBar = instance<Any>()
                        seekBar.asResolver().firstMethod {
                            name = "setProgressColor"
                            parameters(ColorStateList::class)
                            superclass()
                        }.invoke(
                            ColorStateList.valueOf(
                                formatColorAlpha(Color.WHITE, 0.5F)
                            )
                        )
                    }
                }
            }

            //Source VolumeBlurManager
            "com.oplus.systemui.volume.utils.VolumeBlurManager".toClassOrNull()?.resolve()?.apply {
                firstMethod { name = "getBackgroundBlurDrawable" }.hook {
                    after {
                        if (customAlpha < 0) return@after
                        val value = customAlpha * 25
                        val drawable = result<Drawable>()
                        if (drawable is BackgroundBlurDrawable) {
                            drawable.setBlurRadius(value.dp)
                        }
                    }
                }
            }
        }
    }
}