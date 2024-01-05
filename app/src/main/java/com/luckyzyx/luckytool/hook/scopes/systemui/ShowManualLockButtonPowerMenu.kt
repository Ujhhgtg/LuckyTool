package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.os.Handler
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.hook.utils.sysui.DependencyUtils
import com.luckyzyx.luckytool.hook.utils.sysui.FlavorOneFeatureUtils

object ShowManualLockButtonPowerMenu : YukiBaseHooker() {
    @Suppress("SameParameterValue")
    override fun onHook() {
        //Source ShutdownViewControl
        "com.oplus.systemui.shutdown.ShutdownViewControl".toClass().apply {
            method { name = "initManuallyLock" }.hook {
                after {
                    if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@after
                    val context = args().first().cast<Context>() ?: return@after
                    val manuallyLockCanBeSeen = method { name = "manuallyLockCanBeSeen" }
                        .get(instance).invoke<Boolean>(context) ?: return@after
                    val isManuallyLockedOn = getManuallyLockedStatus() ?: return@after
                    val getCurrentUserId =
                        "com.android.systemui.oplusutils.OsBinderCacheUtils".toClass().method {
                            name = "getCurrentUserId"
                        }.get().invoke<Int>()
                    val strongAuthForUser =
                        "com.android.internal.widget.LockPatternUtils".toClass()
                            .buildOf(context) { param(ContextClass) }?.current()
                            ?.method { name = "getStrongAuthForUser" }
                            ?.invoke<Int>(getCurrentUserId)
                    val isChildren = Settings.Global.getInt(
                        context.contentResolver, "children_mode_on", 0
                    ) == 1
                    val isStudy = Settings.Global.getInt(
                        context.contentResolver, "STUDY_CENTER_MODE", 0
                    ) == 1
                    if (manuallyLockCanBeSeen && !isManuallyLockedOn && strongAuthForUser != 1 && (!isChildren || !isStudy)) {
                        field { name = "mShouldShowManuallyLock" }.get(instance).setTrue()
                        field { name = "mOplusShutdownView" }.get(instance).any()
                            ?.current()?.method {
                                name = "setManuallyLockEnable";param(BooleanType)
                            }?.call(true)
                    } else {
                        field { name = "mShouldShowManuallyLock" }.get(instance).setFalse()
                        field { name = "mOplusShutdownView" }.get(instance).any()
                            ?.current()?.method { name = "setManuallyLockEnable" }?.call(false)
                    }
                }
            }
        }

        //Source OplusGlobalActionsDialog
        "com.oplus.systemui.shutdown.OplusGlobalActionsDialog".toClass().apply {
            method { name = "showOrHideDialog" }.hook {
                after {
                    if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@after
                    val mExt = field { name = "mExt" }.get(instance).any() ?: return@after
                    val mLockPatternUtils =
                        field { name = "mLockPatternUtils" }.get(instance).any()
                    val mDialog = field { name = "mDialog" }.get(instance).any()
                    val mShutdownViewControl =
                        field { name = "mShutdownViewControl" }.get(instance).any()
                    mExt.current().method { name = "setForManuallyLock" }
                        .call(mLockPatternUtils, mDialog, mShutdownViewControl)
                    mExt.current().method { name = "registerForManuallyLock" }.call()
                }
            }
        }

        //Source KeyguardUpdateMonitor
        "com.android.keyguard.KeyguardUpdateMonitor".toClass().apply {
            constructor().hook {
                after {
                    if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@after
                    val mContext = field { name = "mContext" }.get(instance).cast<Context>()
                        ?: return@after
                    val mHandler = field { name = "mHandler" }.get(instance).cast<Handler>()
                        ?: return@after
                    getOplusManuallyLock()?.current()?.method { name = "onCreate" }
                        ?.call(mContext, mHandler)
                }
            }
            method { name = "setKeyguardGoingAway" }.hook {
                before {
                    if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@before
                    val isManuallyLockedOn = getManuallyLockedStatus() ?: return@before
                    val bool = args().first().boolean()
                    if (bool && isManuallyLockedOn) setManuallyLockedStatus(false)
                }
            }
        }

        //Source KeyguardViewMediator
        "com.android.systemui.keyguard.KeyguardViewMediator".toClass().apply {
            method { name = "handleStartKeyguardExitAnimation" }.hook {
                after {
                    if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@after
                    val isManuallyLockedOn = getManuallyLockedStatus() ?: return@after
                    if (isManuallyLockedOn) setManuallyLockedStatus(false)
                }
            }
        }
    }

    private fun getOplusManuallyLock(): Any? {
        val shutDownDependency = "com.android.systemui.shutdown.ShutDownDependencyEx".toClass()
        val dependency = DependencyUtils(appClassLoader, true).get(shutDownDependency)
        return dependency?.current()?.method { name = "getOplusManuallyLockEx" }?.call()
    }

    private fun getManuallyLockedStatus(): Boolean? {
        return getOplusManuallyLock()?.current()?.method {
            name = "isManuallyLockedOn"
        }?.invoke<Boolean>()
    }

    @Suppress("SameParameterValue")
    private fun setManuallyLockedStatus(status: Boolean) {
        getOplusManuallyLock()?.current()?.method {
            name = "setManuallyLockedOn"
        }?.call(status)
    }
}