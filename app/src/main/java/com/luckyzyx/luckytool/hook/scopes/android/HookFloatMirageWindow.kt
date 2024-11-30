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

        private val Task = "com.android.server.wm.Task"
        private val ActivityTaskManagerService = "com.android.server.wm.ActivityTaskManagerService"
        private val RootWindowContainer = "com.android.server.wm.RootWindowContainer"

        override fun onHook() {
            var taskId: Int = -1
            var task: Any? = null

            //Source OplusFlexibleDCSManager
            "com.android.server.wm.FlexibleTaskController".toClass().apply {
                method { name = "notifyFlexibleTaskEvent" }.hook {
                    before {
                        taskId = args().first().int()
                        val mAtms = field { type = ActivityTaskManagerService }.get(instance).any()
                            ?: return@before
                        val mRootWindowContainer = mAtms.current().field {
                            type = RootWindowContainer;superClass()
                        }.any() ?: return@before
                        task = mRootWindowContainer.current().method {
                            name = "anyTaskForId"
                            paramCount = 1
                        }.call(taskId) ?: return@before
                    }
                }
                method { name = "exitFlexibleTask" }.hook {
                    before {
                        val curTask = args().first().any() ?: return@before
                        val mTaskId = curTask.current().field { name = "mTaskId" }.int()
                        if (taskId == mTaskId) resultNull()
                    }
                }
            }

            //Source OplusFlexibleDCSManager
            "com.android.server.wm.OplusFlexibleDCSManager".toClass().apply {
                method { name = "onFloatHandleEnter" }.hook {
                    after {
                        val info = args().first().any() ?: return@after
                        val curTaskId = info.current().field { name = "taskId" }.int()
                        val curUserId = info.current().field { name = "userId" }.int()

                        if (taskId != curTaskId) return@after
                        if (task == null) return@after

                        val taskInfo = task!!.current().method {
                            name = "getTaskInfo"
                            returnType = RunningTaskInfo::class.java
                        }.invoke<RunningTaskInfo>() ?: return@after

                        val baseIntent = taskInfo.current().field {
                            type = IntentClass;superClass()
                        }.cast<Intent>() ?: return@after

                        baseIntent.putExtra("TASKINFO_UID", curUserId)

                        startMirageWindow(baseIntent)
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