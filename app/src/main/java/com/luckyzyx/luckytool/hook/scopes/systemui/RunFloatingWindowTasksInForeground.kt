package com.luckyzyx.luckytool.hook.scopes.systemui

import android.app.ActivityManager.RunningTaskInfo
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.startMirageWindow

@Obfuscate
object RunFloatingWindowTasksInForeground : YukiBaseHooker() {

    override fun onHook() {
        var flag = -1
        var status: Boolean

        //Source ZoomStateManager
        "com.oplus.zoom.zoomstate.ZoomStateManager".toClass().apply {
            method { name = "requestChangeZoomTask";param(IntType, BooleanType) }.hook {
                before {
                    flag = args().first().int()
                    status = args().last().boolean()

                    //浮窗全屏 flag 4
                    //浮窗贴边 flag 5
                    //浮窗退出 flag 6

                    if (flag == 5 && status) {
                        val mTaskInfo = field { name = "mTaskInfo" }.get(instance)
                            .cast<RunningTaskInfo>() ?: return@before

                        val baseIntent = mTaskInfo.current().field {
                            type = IntentClass;superClass()
                        }.cast<Intent>() ?: return@before

                        val uid = mTaskInfo.current().field { name = "uid";superClass() }
                            .int()
                        if (uid > 0) baseIntent.putExtra("TASKINFO_UID", uid)

                        startMirageWindow(baseIntent)
                        resultNull()
                    }
                }
            }
        }

        //Source FloatHandleController
        "com.oplus.zoom.ui.floathandle.FloatHandleController".toClass().apply {
            method { name = "onTaskMovedToFront" }.hook {
                before {
                    if (flag == 5) resultNull()
                }
            }
        }
    }
}