package com.luckyzyx.luckytool.hook.scopes.systemui

import android.app.ActivityManager
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.oplus.miragewindow.OplusMirageOptions
import com.oplus.miragewindow.OplusMirageWindowManager

object RunFloatingWindowTasksInForeground : YukiBaseHooker() {

    override fun onHook() {
        var isEnable =
            prefs(ModulePrefs).getBoolean("run_floating_window_tasks_in_foreground", false)
        dataChannel.wait<Boolean>("run_floating_window_tasks_in_foreground") { isEnable = it }

        var flag = -1
        var status: Boolean

        //Source ZoomStateManager
        "com.oplus.zoom.zoomstate.ZoomStateManager".toClass().apply {
            method { name = "requestChangeZoomTask";param(IntType, BooleanType) }.hook {
                before {
                    if (!isEnable) return@before
                    flag = args().first().int()
                    status = args().last().boolean()

                    //浮窗全屏 flag 4
                    //浮窗贴边 flag 5
                    //浮窗退出 flag 6

                    if (flag == 5 && status) {
                        val mTaskInfo = field { name = "mTaskInfo" }.get(instance)
                            .cast<ActivityManager.RunningTaskInfo>() ?: return@before

                        val baseIntent = mTaskInfo.current().field {
                            type = IntentClass;superClass()
                        }.cast<Intent>() ?: return@before

                        val makeBasic = OplusMirageOptions.makeBackgroundStreamModeOptions()
                        OplusMirageWindowManager.getInstance().startMirageWindowMode(
                            baseIntent, makeBasic.toBundle()
                        )
                        resultNull()
                    }
                }
            }
        }

        //Source FloatHandleController
        "com.oplus.zoom.ui.floathandle.FloatHandleController".toClass().apply {
            method { name = "onTaskMovedToFront" }.hook {
                before {
                    if (isEnable && flag == 5) resultNull()
                }
            }
        }
    }
}