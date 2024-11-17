package com.luckyzyx.luckytool.hook.scopes.android

import android.app.ActivityManager.RunningTaskInfo
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.UserHandle
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.OplusMirageDisplayManagerUtils
import com.luckyzyx.luckytool.utils.A15
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.startMirageWindow

@Obfuscate
@Suppress("LocalVariableName")
object HookFloatMirageWindow : YukiBaseHooker() {

    override fun onHook() {
        if (prefs(ModulePrefs).getBoolean("run_floating_window_tasks_in_foreground", false)) {
            if (SDK >= A15) loadHooker(FloatWindowBackRun)
            loadHooker(MultiAppFloatWindowBackRun)
        }
    }

    @Obfuscate
    object FloatWindowBackRun : YukiBaseHooker() {
        override fun onHook() {
            val Task = "com.android.server.wm.Task"

            //Source FlexibleTaskCaptionView
            "com.android.server.wm.FlexibleTaskCaptionView".toClass().apply {
                method { name = "onStateChanged";paramCount = 2 }.hook {
                    after {
                        val preState = args().first().int()
                        val newState = args().last().int()

                        //关闭 1 0
                        //创建 0 1
                        //全屏 1 0
                        //贴边 1 4
                        //贴边恢复 4 1

                        if (preState == 1 && newState == 4) {

                            val task = field { type = Task;superClass() }.get(instance).any()
                                ?: return@after

                            val taskInfo = task.current().method {
                                name = "getTaskInfo"
                                returnType = RunningTaskInfo::class.java
                            }.invoke<RunningTaskInfo>() ?: return@after

                            val baseIntent = taskInfo.current().field {
                                type = IntentClass;superClass()
                            }.cast<Intent>() ?: return@after

                            val uid = taskInfo.current().field { name = "uid";superClass() }
                                .int()
                            if (uid > 0) baseIntent.putExtra("TASKINFO_UID", uid)

                            startMirageWindow(baseIntent)
                        }
                    }
                }
            }

            //Source FloatHandleController
            "com.android.server.wm.FloatHandleController".toClass().apply {
                method { name = "removeFloatHandleInner";paramCount = 3 }.hook {
                    before {
//                        val taskId = args().first().int()
//                        val isNeedAnim = args(1).boolean()
                        val removeFlag = args().last().int()
                        if (removeFlag == 5) resultNull()
                    }
                }
            }
        }
    }

    @Obfuscate
    object MultiAppFloatWindowBackRun : YukiBaseHooker() {
        override fun onHook() {
            val activityTaskManagerService = "com.android.server.wm.ActivityTaskManagerService"
            val OPLUS_MIRAGE_CAR_DUMMY_ACTION = "android.intent.action.OPLUS_MIRAGE_CAR_DUMMY"

            //Source OplusMirageWindowManagerService
            "com.android.server.wm.OplusMirageWindowManagerService".toClass().apply {
                method { name = "startActivityToMirageDisplay" }.hook {
                    before {
                        val parcelable = args().first().cast<Parcelable>()
                        val displayId = args(1).int()
                        val startOptions = args().last().cast<Bundle>()

                        val mAtms = field { type = activityTaskManagerService }.get(instance).any()
                            ?: return@before
                        val context = mAtms.current().field { type = ContextClass }.cast<Context>()
                            ?: return@before

                        val options = startOptions?.let {
                            ActivityOptions::class.java.method {
                                name = "fromBundle";param(BundleClass)
                            }.get().invoke<ActivityOptions>(startOptions)
                        } ?: ActivityOptions.makeBasic()
                        options.setLaunchDisplayId(displayId)

                        Handler(Looper.getMainLooper()).post {
                            var intent: Intent? = null
                            var pendingIntent: PendingIntent? = null

                            try {
                                when (parcelable) {
                                    is Intent -> intent = parcelable
                                    is PendingIntent -> {
                                        pendingIntent = parcelable
                                        intent = pendingIntent.current().method {
                                            name = "getIntent";emptyParam()
                                        }.invoke()
                                    }
                                }

                                if (intent != null) {
                                    if (intent.action != OPLUS_MIRAGE_CAR_DUMMY_ACTION) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        try {
                                            if (pendingIntent == null) {

                                                val uid = intent.getIntExtra("TASKINFO_UID", -1)
                                                if (uid > 0 && uid.toString().startsWith("999")) {
                                                    val userHandle =
                                                        UserHandle.getUserHandleForUid(uid)
                                                    context.current().method {
                                                        name = "startActivityAsUser"
                                                        param(
                                                            IntentClass, BundleClass,
                                                            UserHandleClass
                                                        )
                                                    }.call(intent, options.toBundle(), userHandle)
                                                } else {
                                                    context.startActivity(
                                                        intent, options.toBundle()
                                                    )
                                                }
                                            } else {
                                                pendingIntent.send(
                                                    null, 0, null, null,
                                                    null, null, options.toBundle()
                                                )
                                            }
                                        } catch (_: Exception) {

                                        }
                                    } else {
                                        field { name = "mRealCarDisplayId" }.get(instance)
                                            .set(displayId)
                                    }
                                }
                                OplusMirageDisplayManagerUtils(appClassLoader).apply {
                                    val ins = getInstance() ?: return@post
                                    notifyCastSuccess(ins, displayId)
                                }
                            } catch (_: ActivityNotFoundException) {

                            }
                        }
                        resultNull()
                    }
                }
            }
        }
    }
}