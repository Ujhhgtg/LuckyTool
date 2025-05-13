package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.android.internal.graphics.drawable.BackgroundBlurDrawable
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ColorStateListClass
import com.highcapable.yukihookapi.hook.type.android.DialogInterfaceClass
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
            VariousClass(
                "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
                "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14 C15
            ).toClass().apply {
                val clazz = this
                val hasSurrealQuality = hasMethod { name = "isSurrealQualityOn" }

                val hasVerticalRowsLayerMap = hasField { name = "mVerticalRowsLayerDrawableMap" }

                val hasVerticalRowsLayer = hasField { name = "mVerticalRowsLayerDrawable" }
                val hasVolumeMoreLayer = hasField { name = "mVolumeMoreLayerDrawable" }
                val hasCaptionLayer = hasField { name = "mVolumeCaptionLayerDrawable" }
                val hasAppAdjustLayer = hasField { name = "mVolumeAppAdjustLayerDrawable" }
                val hasVolumeBtnDrawable = hasField { name = "mVolumeBtnDrawable" }
                val hasVolumeBackgroundLayer = hasField { name = "mVolumeBackgroundLayerDrawable" }

                val hasUpdateRowsH = hasMethod { name = "updateRowsH" }
                val hasExpandPanel = hasMethod { name = "expandPanel" }

                if (hasSurrealQuality) method { name = "isSurrealQualityOn" }.hook {
                    replaceToFalse()
                }

                dexKitBridge.findClass {
                    matcher {
                        className(clazz.name, StringMatchType.StartsWith)
                        addMethod {
                            paramTypes(DialogInterfaceClass)
                            usingStrings("initDialog")
                            usingNumbers(0, -1)
                        }
                    }
                }.apply {
                    checkDataList("VolumeDialogBackground find onShow")
                    single().name.toClass().apply {
                        method { param(DialogInterfaceClass) }.hook {
                            before {
                                if (customAlpha < 0) return@before
                                val value = customAlpha * 25

                                val ins = if (hasField { type = clazz }) {
                                    field { type = clazz }.get(instance).any() ?: return@before
                                } else instance

                                if (hasVerticalRowsLayer) ins.current().field {
                                    name = "mVerticalRowsLayerDrawable"
                                }.cast<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                if (hasVolumeMoreLayer) ins.current().field {
                                    name = "mVolumeMoreLayerDrawable"
                                }.cast<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                if (hasAppAdjustLayer) ins.current().field {
                                    name = "mVolumeAppAdjustLayerDrawable"
                                }.cast<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                if (hasCaptionLayer) ins.current().field {
                                    name = "mVolumeCaptionLayerDrawable"
                                }.cast<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                if (hasVolumeBackgroundLayer) ins.current().field {
                                    name = "mVolumeBackgroundLayerDrawable"
                                }.cast<LayerDrawable>()?.apply {
                                    val blurDrawable = getDrawable(0)
                                    if (blurDrawable is BackgroundBlurDrawable) {
                                        blurDrawable.setBlurRadius(value.dp)
                                    }
                                    getDrawable(1)?.alpha = value
                                }
                                if (hasVolumeBtnDrawable) ins.current().field {
                                    name = "mVolumeBtnDrawable"
                                }.cast<Drawable>()?.apply {
                                    alpha = 255 - value
                                }
                            }
                        }
                    }
                }

                if (hasVerticalRowsLayerMap) {
                    if (hasUpdateRowsH) method { name = "updateRowsH" }.hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            field {
                                name = "mVerticalRowsLayerDrawableMap"
                            }.get(instance).cast<HashMap<Int, LayerDrawable>>()?.apply {
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
                if (hasExpandPanel) method { name = "expandPanel" }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        if (hasVolumeBackgroundLayer) field {
                            name = "mVolumeBackgroundLayerDrawable"
                        }.get(instance).cast<LayerDrawable>()?.apply {
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
            "com.oplus.systemui.volume.OplusVolumeSeekBar".toClassOrNull()?.apply {
                constructor().hookAll {
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

            //Source VolumeBlurManager
            "com.oplus.systemui.volume.utils.VolumeBlurManager".toClassOrNull()?.apply {
                method { name = "getBackgroundBlurDrawable" }.hook {
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
            VariousClass(
                "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
                "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14 C15
            ).toClass().apply {
                val clazz = this
                val hasSurrealQuality = hasMethod { name = "isSurrealQualityOn" }

                val hasAddVerticalContainerBg = hasMethod { name = "addVerticalContainerBg" }
                val hasVerticalRowsLayerMap = hasField { name = "mVerticalRowsLayerDrawableMap" }

                val hasVerticalRowsLayer = hasField { name = "mVerticalRowsLayerDrawable" }
                val hasVolumeMoreLayer = hasField { name = "mVolumeMoreLayerDrawable" }
                val hasCaptionLayer = hasField { name = "mVolumeCaptionLayerDrawable" }
                val hasAppAdjustLayer = hasField { name = "mVolumeAppAdjustLayerDrawable" }
                val hasVolumeBtnDrawable = hasField { name = "mVolumeBtnDrawable" }
                val hasVolumeBackgroundLayer = hasField { name = "mVolumeBackgroundLayerDrawable" }

                val hasUpdateRowsH = hasMethod { name = "updateRowsH" }
                val hasExpandPanel = hasMethod { name = "expandPanel" }

                if (hasSurrealQuality) method { name = "isSurrealQualityOn" }.hook {
                    replaceToFalse()
                }

                method { param(DialogInterfaceClass) }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        if (hasVerticalRowsLayer) field {
                            name = "mVerticalRowsLayerDrawable"
                        }.get(
                            instance
                        ).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        if (hasVolumeMoreLayer) field {
                            name = "mVolumeMoreLayerDrawable"
                        }.get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        if (hasAppAdjustLayer) field {
                            name = "mVolumeAppAdjustLayerDrawable"
                        }.get(
                            instance
                        ).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        if (hasCaptionLayer) field {
                            name = "mVolumeCaptionLayerDrawable"
                        }.get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        if (hasVolumeBackgroundLayer) field {
                            name = "mVolumeBackgroundLayerDrawable"
                        }.get(instance).cast<LayerDrawable>()?.apply {
                            val blurDrawable = getDrawable(0)
                            if (blurDrawable is BackgroundBlurDrawable) {
                                blurDrawable.setBlurRadius(value.dp)
                            }
                            getDrawable(1)?.alpha = value
                        }
                        if (hasVolumeBtnDrawable) field {
                            name = "mVolumeBtnDrawable"
                        }.get(instance)
                            .cast<Drawable>()?.apply {
                                alpha = 255 - value
                            }
                    }
                }

                if (hasVerticalRowsLayerMap) {
                    method {
                        name = if (hasAddVerticalContainerBg) "addVerticalContainerBg"
                        else "updateVolumeRowBgForSide"
                    }.hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            field {
                                name = "mVerticalRowsLayerDrawableMap"
                            }.get(instance).cast<HashMap<Int, LayerDrawable>>()?.apply {
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
                    if (hasUpdateRowsH) method { name = "updateRowsH" }.hook {
                        before {
                            if (customAlpha < 0) return@before
                            val value = customAlpha * 25
                            field {
                                name = "mVerticalRowsLayerDrawableMap"
                            }.get(instance).cast<HashMap<Int, LayerDrawable>>()?.apply {
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
                if (hasExpandPanel) method { name = "expandPanel" }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        val value = customAlpha * 25
                        if (hasVolumeBackgroundLayer) field {
                            name = "mVolumeBackgroundLayerDrawable"
                        }.get(instance).cast<LayerDrawable>()?.apply {
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

            //Source VolumeBlurManager
            "com.oplus.systemui.volume.utils.VolumeBlurManager".toClassOrNull()?.apply {
                method { name = "getBackgroundBlurDrawable" }.hook {
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