package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.DoubleClickLockScreen
import com.luckyzyx.luckytool.hook.scopes.systemui.VibrateWhenOpeningTheStatusBar
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookStatusBar : YukiBaseHooker() {
    override fun onHook() {
        //双击状态栏锁屏
        if (prefs(ModulePrefs).getBoolean("statusbar_double_click_lock_screen", false)) {
            loadHooker(DoubleClickLockScreen)
        }
        //打开状态栏时振动
        if (prefs(ModulePrefs).getBoolean("vibrate_when_opening_the_statusbar", false)) {
            loadHooker(VibrateWhenOpeningTheStatusBar)
        }

        //Source OplusToggleSliderView
//        "com.oplus.systemui.qs.widget.OplusToggleSliderView".toClass().apply {
//            method { name = "onShapeChanged" }.hook {
//                after {
//                    val type = args().first().int()
//                    val mSlider = field { name = "mSlider" }.get(instance).any() ?: return@after
//                    val mThumbColorStateList = mSlider.current().field {
//                        name = "mThumbColorStateList"
//                    }.cast<ColorStateList>() ?: return@after
//                    val newColorStateList = mThumbColorStateList.withAlpha(50)
//                    mSlider.current().method { name = "setThumbColor" }.call(newColorStateList)
//                }
//            }
//        }

        //res/layout/bubble_expanded_view.xml
        //<string name="bubble_close">关闭对话</string>
        //<string name="open_app">进入应用</string>
        //flag_conversations
        //bubble_ic_create_bubble 箭头向右下
        //bubble_ic_stop_bubble 箭头向左上

//        "com.android.wm.shell.bubbles.BubbleController".toClass().apply {
//            method { name = "isResizableActivity" }.hook {
//                replaceToTrue()
//            }
//        }
//
//        "com.android.systemui.statusbar.notification.row.NotificationContentView".toClass().apply {
//            method { name = "shouldShowBubbleButton" }.hook {
//                before {
//                    val entry = args().first().any() ?: return@before
//                    result = entry.current().method {
//                        name = "getBubbleMetadata";emptyParam()
//                    }.call() != null
//                    YLog.debug("${method.name} -> $result")
//                }
//            }
//        }
//
//        "com.android.systemui.wmshell.BubblesManager".toClass().apply {
//            method { name = "areBubblesEnabled" }.hook {
//                replaceToTrue()
//            }
//        }

    }
}
