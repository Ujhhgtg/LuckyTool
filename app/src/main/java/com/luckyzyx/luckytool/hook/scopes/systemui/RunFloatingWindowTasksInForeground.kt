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

        //Source ZoomStateManager
        "com.oplus.zoom.zoomstate.ZoomStateManager".toClass().apply {
            method { name = "requestChangeZoomTask";param(IntType, BooleanType) }.hook {
                before {
                    if (!isEnable) return@before
                    val int = args().first().int()
                    val bool = args().last().boolean()

                    val mTaskInfo = field { name = "mTaskInfo" }.get(instance)
                        .cast<ActivityManager.RunningTaskInfo>() ?: return@before
                    val intent = mTaskInfo.current().field {
                        type = IntentClass;superClass()
                    }.cast<Intent>() ?: return@before

                    if (int == 5 && bool) {
                        val makeBasic = OplusMirageOptions.makeBasic()
                        makeBasic.setCastMode(4)
                        OplusMirageWindowManager.getInstance().startMirageWindowMode(
                            intent, makeBasic.toBundle()
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
                    if (isEnable) resultNull()
                }
            }
        }
    }
}