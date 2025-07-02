package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveTopLockScreenIcon : YukiBaseHooker() {
    override fun onHook() {
        val LockIconView = "com.android.keyguard.LockIconView"

        //Source LockIcon
        "com.android.systemui.statusbar.phone.LockIcon".toClassOrNull()?.resolve()?.apply {
            firstMethod { name = "updateIconVisibility" }.hook {
                before {
                    args().first().setFalse()
                }
            }
        }

        //Source LockIcon C14
        LockIconView.toClassOrNull()?.resolve()?.apply {
            firstMethod { name = "updateColorAndBackgroundVisibility" }.hook {
                after {
                    firstField { name = "mLockIcon" }.of(instance).get<View>()?.isVisible = false
                }
            }
        }

        //Source LegacyLockIconViewController C15+
        "com.android.keyguard.LegacyLockIconViewController".toClassOrNull()?.resolve()?.apply {
            firstMethod { name { it.startsWith("updateVisibility") } }.hook {
                before {
                    firstField { type = LockIconView }.of(instance).get<View>()?.isVisible = false
                    resultNull()
                }
            }
        }
    }
}