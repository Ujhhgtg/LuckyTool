package com.luckyzyx.luckytool.hook.scope.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

object RemoveTopLockScreenIcon : YukiBaseHooker() {
    override fun onHook() {
        //Source LockIcon
        "com.android.systemui.statusbar.phone.LockIcon".toClass().apply {
            method { name = "updateIconVisibility" }.hook {
                before { args().first().setFalse() }
            }
        }
        //Source LockIcon C14
        "com.android.keyguard.LockIconView".toClassOrNull()?.apply {
            method { name = "updateColorAndBackgroundVisibility" }.hook {
                after {
                    field { name = "mLockIcon" }.get(instance).cast<View>()?.isVisible = false
                }
            }
        }
    }
}