package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BitmapClass
import com.highcapable.yukihookapi.hook.type.android.PaintClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object FullScreenGestureSideSlideBar : YukiBaseHooker() {
    override fun onHook() {
        //Source SideGestureViewManager
        //Source SideGestureNavView
        val removeView = prefs(ModulePrefs).getBoolean("remove_side_slider", false)
        val removeBackground =
            prefs(ModulePrefs).getBoolean("remove_side_slider_black_background", false)
        val isReplace = prefs(ModulePrefs).getBoolean("replace_side_slider_icon_switch", false)
        val leftPath = prefs(ModulePrefs).getString("replace_side_slider_icon_on_left", "")
        val rightPath = prefs(ModulePrefs).getString("replace_side_slider_icon_on_right", "")
        VariousClass(
            "com.oplusos.systemui.navbar.gesture.sidegesture.SideGestureNavView", //A11
            "com.oplusos.systemui.navigationbar.gesture.sidegesture.SideGestureNavView",
            "com.oplus.systemui.navigationbar.gesture.sidegesture.SideGestureNavView" //C14
        ).toClass().apply {
            method { name = "onDraw";paramCount = 1 }.hook {
                if (removeView) intercept()
            }
            method { name = "initPaint";emptyParam() }.hook {
                after {
                    if (!removeBackground) return@after
                    field { name = "mBezierPaint";type = PaintClass }.get(instance)
                        .cast<Paint>()?.color = Color.TRANSPARENT
                }
            }
            method { name = "setBackIcon";param(BitmapClass) }.hook {
                before {
                    if (!isReplace) return@before
                    val bitmap = when (field { name = "mPosition" }.get(instance).int()) {
                        0 -> BitmapFactory.decodeFile(leftPath)
                        1 -> BitmapFactory.decodeFile(rightPath)
                        else -> return@before
                    }
                    bitmap?.let { args().first().set(it) }
                }
            }
        }
    }
}