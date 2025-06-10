@file:Suppress("DEPRECATION")

package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Handler
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ComponentNameClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.hook.utils.sysui.DependencyUtils
import com.luckyzyx.luckytool.hook.utils.sysui.FlavorOneFeatureUtils
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ShowManualLockButtonPowerMenu : YukiBaseHooker() {

    val OsBinderCacheUtils = "com.android.systemui.oplusutils.OsBinderCacheUtils"
    val LockPatternUtils = "com.android.internal.widget.LockPatternUtils"

    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 35) loadHooker(ManualLockButton)
        else loadHooker(ManualLockButtonV14)
    }

    @Obfuscate
    object ManualLockButton : YukiBaseHooker() {
        @SuppressLint("MissingPermission")
        override fun onHook() {
            //Source ShutdownViewControl
            "com.oplus.systemui.shutdown.ShutdownViewControl".toClass().apply {
                constructor { param(ContextClass) }.hook {
                    after {
                        if (FlavorOneFeatureUtils(appClassLoader).isFlavorOneDevice() == true) return@after
                        val context = args().first().cast<Context>() ?: return@after
                        val isManuallyLockedOn = getManuallyLockedStatus() ?: return@after
                        val getCurrentUserId = OsBinderCacheUtils.toClass().method {
                            name = "getCurrentUserId"
                        }.get().int()
                        val strongAuthForUser = LockPatternUtils.toClass()
                            .buildOf(context) { param(ContextClass) }?.current()
                            ?.method { name = "getStrongAuthForUser" }
                            ?.invoke<Int>(getCurrentUserId)
                        val isChildren = Settings.Global.getInt(
                            context.contentResolver, "children_mode_on", 0
                        ) == 1
                        val isStudy = Settings.Global.getInt(
                            context.contentResolver, "STUDY_CENTER_MODE", 0
                        ) == 1
                        if (manuallyLockCanBeSeen(context) && !isManuallyLockedOn && strongAuthForUser != 1 && (!isChildren || !isStudy)) {
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
                        val listener = mExt.current().field { name = "mOnManuallyLock" }.any()

                        val mLockPatternUtils =
                            field { name = "mLockPatternUtils" }.get(instance).any()
                        mExt.current().field { name = "mLockPatternUtils" }.set(mLockPatternUtils)
                        val mDialog = field { name = "mDialog" }.get(instance).any()
                        mExt.current().field { name = "mDialog" }.set(mDialog)

                        val mShutdownViewControl =
                            field { name = "mShutdownViewControl" }.get(instance).any()
                        mShutdownViewControl?.current()?.method {
                            name = "setOnManuallyLockListener"
                        }?.call(listener)
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
    }

    @Obfuscate
    object ManualLockButtonV14 : YukiBaseHooker() {
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
                        val getCurrentUserId = OsBinderCacheUtils.toClass().method {
                            name = "getCurrentUserId"
                        }.get().invoke<Int>()
                        val strongAuthForUser = LockPatternUtils.toClass()
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
    }

    @SuppressLint("MissingPermission")
    fun manuallyLockCanBeSeen(context: Context): Boolean {
        val currentUserId = OsBinderCacheUtils.toClass().method {
            name = "getCurrentUserId"
        }.get().int()
        val faceManager = context.getSystemService("face")
        val hasEnrolledTemplates = faceManager.current().method {
            name = "hasEnrolledTemplates";param(IntType)
        }.boolean(currentUserId)

        val z = faceManager != null && hasEnrolledTemplates && Settings.Secure.getInt(
            context.contentResolver, "oplus_customize_face_unlock_switch", -1
        ) == 1
        val i2 = Settings.Secure.getInt(
            context.contentResolver, "oplus_customize_fingerprint_unlock_switch", -1
        )
        val fingerprintManager = context.getSystemService(FingerprintManager::class.java)
        val hasEnrolledFingerprints = fingerprintManager.current().method {
            name = "hasEnrolledFingerprints";param(IntType)
        }.boolean(currentUserId)
        return z || (fingerprintManager.isHardwareDetected && hasEnrolledFingerprints && i2 == 1 &&
                !isFpDisabledByDPM(context, currentUserId))
    }

    private fun isFpDisabledByDPM(context: Context, userId: Int): Boolean {
        val service = context.getSystemService("device_policy") as DevicePolicyManager
        val getKeyguardDisabledFeatures = service.current().method {
            name = "getKeyguardDisabledFeatures";param(ComponentNameClass, IntType)
        }.int(null, userId)
        return (getKeyguardDisabledFeatures and 32) != 0
    }

    private fun getOplusManuallyLock(): Any? {
        val shutDownDependency = "com.android.systemui.shutdown.ShutDownDependencyEx".toClass()
        val dependency = DependencyUtils(appClassLoader, true).getDependency(shutDownDependency)
        return dependency?.current()?.method { name = "getOplusManuallyLockEx" }?.call()
    }

    private fun getManuallyLockedStatus(): Boolean? {
        return getOplusManuallyLock()?.current()?.method {
            name = "isManuallyLockedOn"
        }?.boolean()
    }

    @Suppress("SameParameterValue")
    private fun setManuallyLockedStatus(status: Boolean) {
        getOplusManuallyLock()?.current()?.method {
            name = "setManuallyLockedOn"
        }?.call(status)
    }
}