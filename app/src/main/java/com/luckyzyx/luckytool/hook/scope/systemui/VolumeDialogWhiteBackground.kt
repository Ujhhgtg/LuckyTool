package com.luckyzyx.luckytool.hook.scope.systemui

import android.graphics.drawable.LayerDrawable
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.DialogInterfaceClass
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.dp

object VolumeDialogWhiteBackground : YukiBaseHooker() {
    override fun onHook() {
        var customAlpha =
            prefs(ModulePrefs).getInt("custom_volume_dialog_background_transparency", -1)
        dataChannel.wait<Int>("custom_volume_dialog_background_transparency") {
            customAlpha = it
        }
        //高斯模糊
        val enableBlur = prefs(ModulePrefs).getBoolean("force_enable_systemui_blur_feature", false)

        //Source VolumeDialogImplEx
        VariousClass(
            "com.oplusos.systemui.volume.VolumeDialogImplEx", //C13
            "com.oplus.systemui.volume.OplusVolumeDialogImpl" //C14
        ).toClass().apply {
            method { name = "isSurrealQualityOn" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    resultFalse()
                }
            }
            method { param(DialogInterfaceClass) }.hook {
                before {
                    if (customAlpha < 0) return@before
                    val value = customAlpha * 25
                    field { name = "mVerticalRowsLayerDrawable" }.get(instance)
                        .cast<LayerDrawable>()?.apply {
                            getDrawable(0)?.setBlurRadius(value.dp)
                            getDrawable(1)?.alpha = value
                        }
                    field { name = "mVolumeMoreLayerDrawable" }.get(instance).cast<LayerDrawable>()
                        ?.apply {
                            getDrawable(0)?.setBlurRadius(value.dp)
                            getDrawable(1)?.alpha = value
                        }
                    if (hasField { name = "mVolumeCaptionLayerDrawable" }.not()) return@before
                    field { name = "mVolumeCaptionLayerDrawable" }.get(instance)
                        .cast<LayerDrawable>()
                        ?.apply {
                            getDrawable(0)?.setBlurRadius(value.dp)
                            getDrawable(1)?.alpha = value
                        }
                }
            }
        }

        if (SDK >= A13) return
        //Source RegionalGaussBlurController C12
        "com.oplusos.util.blur.RegionalGaussBlurController".toClassOrNull()?.apply {
            method { name = "getNormalBannerShouldDisableBlur" }.hook {
                if (enableBlur) replaceToFalse()
            }
        }
    }

    private fun Any.setBlurRadius(blurRadius: Int) {
        current().method { name = "setBlurRadius";paramCount = 1 }.call(blurRadius)
    }
}