package com.luckyzyx.luckytool.hook.scopes.launcher

import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method

object AllowLockingUnLockingOfExcludedActivity : YukiBaseHooker() {
    override fun onHook() {
        //Search OplusTaskShortcutsFactory -> showLock / showUnlock C13
        //Search OplusLockManager -> isTaskAllowLock / isTaskAllowUnlock C14
        //Source OplusLockManager -> Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS (8388608 / 0x00800000)
        "com.oplus.quickstep.applock.OplusLockManager".toClass().apply {
            val methodName = if (hasMethod { name = "isAppLockable" }) "isAppLockable"
            else "isAppSupportLock"
            method { name = methodName }.hook {
                before {
                    val intent = args().last().cast<Intent>() ?: return@before
                    val flag = intent.flags
                    if (flag and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS == Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) {
                        intent.removeFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    }
                }
            }
        }
    }
}