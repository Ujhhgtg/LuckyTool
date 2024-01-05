package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method

object UnlockTaskLocks : YukiBaseHooker() {
    override fun onHook() {
        val appLockModel = "com.oplus.quickstep.applock.AppLockModel".toClassOrNull()
        val isNew = appLockModel?.hasField { name = "noDefaultLockedAppLimit" } ?: false

        if (isNew) {
            appLockModel?.apply {
                method { name = "initData" }.hook{
                    after { field { name = "noDefaultLockedAppLimit" }.get(instance).set(999) }
                }
                method { name = "updateNoDefaultLockAppLimit" }.hook{
                    after { field { name = "noDefaultLockedAppLimit" }.get(instance).set(999) }
                }
            }
            return
        }

        //Source OplusLockManager
        VariousClass(
            "com.coloros.quickstep.applock.ColorLockManager",
            "com.oplus.quickstep.applock.OplusLockManager"
        ).toClass().apply {
            constructor { paramCount = 1 }.hook {
                after {
                    field { name = "mLockAppLimit" }.get(instance).set(999)
                }
            }
        }
    }
}