package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.view.View
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.sysui.QSFeatureOptionUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.getScreenOrientation

@Obfuscate
object SpecialTileTopGap : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        var top = prefs(ModulePrefs).getInt("control_center_special_tile_top_gap", 10)
        dataChannel.wait<Int>("control_center_special_tile_top_gap") { top = it }
        var bottom = prefs(ModulePrefs).getInt("control_center_special_tile_bottom_gap", 0)
        dataChannel.wait<Int>("control_center_special_tile_bottom_gap") { bottom = it }
        var smallBrightness =
            prefs(ModulePrefs).getBoolean("decrease_horizontal_brightness_bar_top_gap", false)
        dataChannel.wait<Boolean>("decrease_horizontal_brightness_bar_top_gap") {
            smallBrightness = it
        }

        val isSupportVolumeSeekBar = QSFeatureOptionUtils(appClassLoader).isSupportVolumeSeekBar()
        if (isSupportVolumeSeekBar) return

        //Source OplusQSTileMediaContainerController
        VariousClass(
            "com.oplusos.systemui.qs.OplusQSTileMediaContainerController", //C13
            "com.oplus.systemui.qs.OplusQSTileMediaContainerController" //C14 C15
        ).toClass().apply {
            method { name = "updateResources" }.hook {
                after {
                    val context = method {
                        name = "getContext";superClass()
                    }.get(instance).invoke<Context>() ?: return@after
                    getScreenOrientation(context) {
                        if (it) return@getScreenOrientation
                        field { name = "mTopGap" }.get(instance).set(top.dp)
                        field { name = "mBottomGap" }.get(instance).set(bottom.dp)
                    }
                }
            }
        }

        if (osCode < 30) return

        //Source OplusQSBottomImpl C14 C15
        "com.oplus.systemui.qs.OplusQSBottomImpl".toClass().apply {
            method { name = "updateIndicator" }.hook {
                before {
                    if (!smallBrightness) return@before
                    val view = instance<View>()
                    getScreenOrientation(view) {
                        if (it) return@getScreenOrientation
                        args().first().setTrue()
                    }
                }
            }
        }
    }
}