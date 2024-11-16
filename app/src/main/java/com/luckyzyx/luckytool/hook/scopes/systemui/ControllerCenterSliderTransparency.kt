package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ColorStateListClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A15
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.formatColorAlpha

@Obfuscate
object ControllerCenterSliderTransparency : YukiBaseHooker() {
    override fun onHook() {
        val customAlpha = prefs(ModulePrefs).getInt("custom_control_center_silder_transparency", -1)

        //Source OplusToggleSliderView C14.0
        VariousClass(
            "com.oplusos.systemui.qs.widget.OplusToggleSliderView", //C13
            "com.oplus.systemui.qs.widget.OplusToggleSliderView", //C14.0
        ).toClassOrNull()?.apply {
            method { name = "setupSliderProgressDrawable" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val value = customAlpha / 10F
                    val mSlider = field { name = "mSlider" }.get(instance).any() ?: return@after
                    val baseProgressColor = mSlider.current().field {
                        name = "mProgressColor";superClass()
                    }.int()
                    mSlider.current().method {
                        name = "setProgressColor"
                        param(ColorStateListClass)
                        superClass()
                    }.call(
                        ColorStateList.valueOf(
                            formatColorAlpha(baseProgressColor, value)
                        )
                    )
                    mSlider.current().method {
                        name = "setThumbColor"
                        param(ColorStateListClass)
                        superClass()
                    }.call(ColorStateList.valueOf(Color.TRANSPARENT))
                }
            }
            method { name = "updateToggleBackground" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val value = 255 / 10 * customAlpha
                    val mToggle = field { name = "mToggle" }.get(instance).cast<CheckBox>()
                        ?: return@after
                    mToggle.background.alpha = value
                }
            }
        }

        //Source OplusQsToggleSliderLayout C14.0.1 C15.0
        "com.oplus.systemui.qs.widget.OplusQsToggleSliderLayout".toClassOrNull()?.apply {
            method {
                name = "generateSliderView"
                if (SDK >= A15) superClass()
            }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val value = customAlpha / 10.0F
                    val seekBar = result<View>() ?: return@after
                    val baseColor = seekBar.current().field { name = "mProgressColor" }.int()
                    seekBar.current().method {
                        name = "setProgressColor"
                        param(ColorStateListClass)
                    }.call(
                        ColorStateList.valueOf(
                            formatColorAlpha(baseColor, value)
                        )
                    )
                }
            }
        }
    }
}