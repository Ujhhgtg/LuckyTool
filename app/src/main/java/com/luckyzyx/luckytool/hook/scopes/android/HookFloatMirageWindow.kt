package com.luckyzyx.luckytool.hook.scopes.android

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
import com.luckyzyx.luckytool.hook.utils.OplusMirageDisplayManagerUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookFloatMirageWindow : YukiBaseHooker() {

    override fun onHook() {
        val isEnable =
            prefs(ModulePrefs).getBoolean("run_floating_window_tasks_in_foreground", false)
        if (!isEnable) return

        val activityTaskManagerService = "com.android.server.wm.ActivityTaskManagerService"
        val OPLUS_MIRAGE_CAR_DUMMY_ACTION = "android.intent.action.OPLUS_MIRAGE_CAR_DUMMY"

        //Source OplusMirageWindowManagerService
        "com.android.server.wm.OplusMirageWindowManagerService".toClass().apply {
            method { name = "startActivityToMirageDisplay" }.hook {
                before {
                    val parcelable = args().first().cast<Parcelable>()
                    val displayId = args(1).int()
                    val startOptions = args().last().cast<Bundle>()
//                    YLog.debug("$simpleName: ${method.name} (${parcelable != null} | $displayId | ${startOptions != null})")

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

                            val uid = intent?.getIntExtra("TAKSINFO_UID", -1) ?: -1
//                            YLog.debug("$simpleName: ${method.name} -> $uid")

                            if (intent != null) {
                                if (intent.action != OPLUS_MIRAGE_CAR_DUMMY_ACTION) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    try {
                                        if (pendingIntent == null) {
                                            if (uid.toString().startsWith("999")) {
                                                val userHandle = UserHandle.getUserHandleForUid(uid)
//                                                YLog.debug("$simpleName: ${method.name} -> $userHandle")

                                                context.current().method {
                                                    name = "startActivityAsUser"
                                                    param(IntentClass, BundleClass, UserHandleClass)
                                                }.call(intent, options.toBundle(), userHandle)
                                            } else {
                                                context.startActivity(intent, options.toBundle())
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