package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getScreenOrientation

object SpecialTileTopGap : YukiBaseHooker() {
    override fun onHook() {
        var top = prefs(ModulePrefs).getInt("control_center_special_tile_top_gap", 0)
        dataChannel.wait<Int>("control_center_special_tile_top_gap") { top = it }
        var bottom = prefs(ModulePrefs).getInt("control_center_special_tile_bottom_gap", 0)
        dataChannel.wait<Int>("control_center_special_tile_bottom_gap") { bottom = it }

        //Source OplusQSTileMediaContainerController
        VariousClass(
            "com.oplusos.systemui.qs.OplusQSTileMediaContainerController", //C13
            "com.oplus.systemui.qs.OplusQSTileMediaContainerController" //C14
        ).toClass().apply {
            method { name = "updateResources" }.hook {
                after {
                    val context = method { name = "getContext";superClass() }.get(instance)
                        .invoke<Context>() ?: return@after
                    getScreenOrientation(context) {
                        if (it) return@getScreenOrientation
                        field { name = "mTopGap" }.get(instance).set(top.dp)
                        field { name = "mBottomGap" }.get(instance).set(bottom.dp)
                    }
                }
            }
        }
    }
}