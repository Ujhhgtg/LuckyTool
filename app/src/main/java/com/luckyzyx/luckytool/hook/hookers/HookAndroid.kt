package com.luckyzyx.luckytool.hook.hookers

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Binder
import android.util.Pair
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalSystemProperties
import com.luckyzyx.luckytool.hook.scopes.android.ADBInstallConfirm
import com.luckyzyx.luckytool.hook.scopes.android.AllowUntrustedTouch
import com.luckyzyx.luckytool.hook.scopes.android.AppSplashScreen
import com.luckyzyx.luckytool.hook.scopes.android.BatteryOptimizationWhitelist
import com.luckyzyx.luckytool.hook.scopes.android.DarkModeService
import com.luckyzyx.luckytool.hook.scopes.android.EnableVideoMemcFrameInsertion
import com.luckyzyx.luckytool.hook.scopes.android.ForceAllAppsSupportSplitScreen
import com.luckyzyx.luckytool.hook.scopes.android.HookAppStartForbidden
import com.luckyzyx.luckytool.hook.scopes.android.HookMediaProjectionManager
import com.luckyzyx.luckytool.hook.scopes.android.HookOplusWifiService
import com.luckyzyx.luckytool.hook.scopes.android.HookWindowManagerService
import com.luckyzyx.luckytool.hook.scopes.android.LTPODynamicRefreshRate
import com.luckyzyx.luckytool.hook.scopes.android.MediaVolumeLevel
import com.luckyzyx.luckytool.hook.scopes.android.MultiAppConfig
import com.luckyzyx.luckytool.hook.scopes.android.OplusWindowSecureFlag
import com.luckyzyx.luckytool.hook.scopes.android.RemoveAccessDeviceLogDialog
import com.luckyzyx.luckytool.hook.scopes.android.RemoveAppUninstallButtonBlackList
import com.luckyzyx.luckytool.hook.scopes.android.RemovePasswordTimeoutVerification
import com.luckyzyx.luckytool.hook.scopes.android.RemoveStatusBarTopNotification
import com.luckyzyx.luckytool.hook.scopes.android.RemoveVPNActiveNotification
import com.luckyzyx.luckytool.hook.scopes.android.ScrollToTopWhiteList
import com.luckyzyx.luckytool.hook.scopes.android.SetAppUpdateDotDisplayMode
import com.luckyzyx.luckytool.hook.scopes.android.SystemEnableVolumeKeyControlFlashlight
import com.luckyzyx.luckytool.hook.scopes.android.ZoomWindowConfig
import com.luckyzyx.luckytool.utils.getOSVersionCode


object HookAndroid : YukiBaseHooker() {

    override fun onHook() {
        val osCode = getOSVersionCode

        loadHooker(HookGlobalFeatureConfig)
        loadHooker(HookGlobalSystemProperties)

        //禁止App启动
        loadHooker(HookAppStartForbidden)

        //移除状态栏上层警告
        loadHooker(RemoveStatusBarTopNotification)

        //移除VPN已激活通知
        loadHooker(RemoveVPNActiveNotification)

        //Oplus Wifi Service
        loadHooker(HookOplusWifiService)

        //Hook HookWindowManagerService
        loadHooker(HookWindowManagerService)

        //音量阶数
        loadHooker(MediaVolumeLevel)

        //应用分身限制
        loadHooker(MultiAppConfig)

        //USB安装确认
        loadHooker(ADBInstallConfirm)

        //移除72小时密码验证
        loadHooker(RemovePasswordTimeoutVerification)

        //移除系统截屏延迟
//        loadHooker(RemoveSystemScreenshotDelay)

        //移除遮罩Splash Screen
        if (osCode >= 26) loadHooker(AppSplashScreen)

        //允许不受信任的触摸
        if (osCode >= 23) loadHooker(AllowUntrustedTouch)

        //缩放窗口
        loadHooker(ZoomWindowConfig)

        //暗色模式服务
        loadHooker(DarkModeService)

        //电池优化白名单
        loadHooker(BatteryOptimizationWhitelist)

        //允许APP回到顶部
        if (osCode >= 26) loadHooker(ScrollToTopWhiteList)

        //禁用访问设备日志对话框
        if (osCode >= 26) loadHooker(RemoveAccessDeviceLogDialog)

        //LTPO动态刷新率
        loadHooker(LTPODynamicRefreshRate)

        //启用音量键控制手电筒手势
        loadHooker(SystemEnableVolumeKeyControlFlashlight)

        //强制所有应用支持分屏
        if (osCode >= 26) loadHooker(ForceAllAppsSupportSplitScreen)

        //移除应用禁止卸载黑名单
        if (osCode >= 26) loadHooker(RemoveAppUninstallButtonBlackList)

        //三方应用通话录音保护
        if (osCode >= 30) loadHooker(HookMediaProjectionManager)

        //视频动态插帧
        loadHooker(EnableVideoMemcFrameInsertion)

        //安全窗口标志
        loadHooker(OplusWindowSecureFlag)

        //App图标更新圆点
        if (osCode >= 33) loadHooker(SetAppUpdateDotDisplayMode)

        //三段式按键
//        loadHooker(HookAlertSlider)

        //Source OplusAppStartConfirmManager
     if (false)   "com.android.server.wm.OplusAppStartConfirmManager".toClass().apply {
         if (false) method { name = "checkStartActivityForConfirm";paramCount = 9 }.hook {
                after {
                    YLog.info("checkStartActivityForConfirm after start")

                    val sourceRecord = args(0).any()
                    val activityInfo = args(1).cast<ActivityInfo>() ?: return@after
                    val sourceIntent = args(2).cast<Intent>() ?: return@after
                    val requestCode = args(3).int()
                    val realCallingUid = args(4).int()
                    val callerPkg = args(5).string()
                    val activityOptions = args(6).cast<ActivityOptions>()
                    val profilerInfo = args(7).any()
                    val abort = args(8).boolean()

                    YLog.info("checkStartActivityForConfirm params ok")

                    val isPreLoad = activityOptions != null && activityOptions.current().method {
                        name = "getLaunchWindowingMode"
                    }.int() == 7

                    if (activityInfo.applicationInfo == null || activityInfo.applicationInfo.packageName == null) {
                        YLog.error("checkStartActivityForConfirm application is null return")
                        return@after
                    }

                    if (abort || isPreLoad) {
                        YLog.error("checkStartActivityForConfirm abort: $abort or isPreLoad: $isPreLoad return")
                        return@after
                    }

                    val activityTaskManagerService = field {
                        type = "com.android.server.wm.ActivityTaskManagerService"
                    }.get(instance).any() ?: return@after

                    val mWindowManager = activityTaskManagerService.current().field {
                        type = "com.android.server.wm.WindowManagerService"
                        if (activityTaskManagerService.javaClass.hasField {
                                type = "com.android.server.wm.WindowManagerService"
                            }.not()) superClass()
                    }.any() ?: return@after

                    val mDisplayEnabled = mWindowManager.current().field {
                        name = "mDisplayEnabled"
                        if (mWindowManager.javaClass.hasField {
                                name = "mDisplayEnabled"
                            }.not()) superClass()
                    }.boolean()

                    if (!mDisplayEnabled) {
                        YLog.error("checkStartActivityForConfirm mDisplayEnabled return")
                        return@after
                    }

                    val mHasConformActivity = field { name = "mHasConformActivity" }.get(instance)
                        .boolean()

                    if (!mHasConformActivity) {
                        YLog.error("checkStartActivityForConfirm mHasConformActivity return")
                        return@after
                    }

                    val isInterceptActivityStart =
                        method { name = "isInterceptActivityStart" }.get(instance).boolean()
                    if (!isInterceptActivityStart) {
                        YLog.error("checkStartActivityForConfirm isInterceptActivityStart return")
                        return@after
                    }

                    val mIntentExt = sourceIntent.current().field {
                        type = "android.content.IIntentExt"
                    }.any() ?: return@after

                    val getOplusFlags = mIntentExt.current().method { name = "getOplusFlags" }.int()

                    if ((getOplusFlags and 131072) != 0) {
                        YLog.error("checkStartActivityForConfirm getOplusFlags return")
                        return@after
                    }

                    val isStartOnMirageDisplay = method { name = "isStartOnMirageDisplay" }
                        .get(instance).boolean(sourceRecord, activityOptions)
                    if (isStartOnMirageDisplay) {
                        YLog.error("checkStartActivityForConfirm isStartOnMirageDisplay return")
                        return@after
                    }

                    val checkAndUpdateStartHistory = method { name = "checkAndUpdateStartHistory" }
                        .get(instance).boolean(activityInfo, sourceIntent, callerPkg)
                    if (checkAndUpdateStartHistory) {
                        YLog.error("checkStartActivityForConfirm checkAndUpdateStartHistory return")
                        return@after
                    }

                    val isSendOrPickAction = method { name = "isSendOrPickAction" }
                        .get(instance).boolean(sourceIntent)
                    if (isSendOrPickAction) {
                        YLog.error("checkStartActivityForConfirm isSendOrPickAction return")
                        return@after
                    }

                    val isCalledFromHome = method { name = "isCalledFromHome" }
                        .get(instance).boolean(sourceRecord)
                    if (isCalledFromHome) {
                        YLog.error("checkStartActivityForConfirm isCalledFromHome return")
                        return@after
                    }

                    val isSystemAppOrSameApp = method { name = "isSystemAppOrSameApp" }
                        .get(instance).boolean(realCallingUid, callerPkg, activityInfo)
                    if (isSystemAppOrSameApp) {
                        YLog.error("checkStartActivityForConfirm isSystemAppOrSameApp return")
                        return@after
                    }

                    val isMultiWindowMode = method { name = "isMultiWindowMode" }
                        .get(instance).boolean(sourceRecord, activityOptions)
                    if (isMultiWindowMode) {
                        YLog.error("checkStartActivityForConfirm isMultiWindowMode return")
                        return@after
                    }

                    val isAppOrActivityHasExist = method { name = "isAppOrActivityHasExist" }
                        .get(instance).boolean(sourceRecord, activityInfo)
                    if (isAppOrActivityHasExist) {
                        YLog.error("checkStartActivityForConfirm isAppOrActivityHasExist return")
                        return@after
                    }

                    val skipLabActivityStartConfirm = method {
                        name = "skipLabActivityStartConfirm"
                    }.get(instance).boolean(sourceRecord, callerPkg, activityInfo, sourceIntent)
                    if (skipLabActivityStartConfirm) {
                        YLog.error("checkStartActivityForConfirm skipLabActivityStartConfirm return")
                        return@after
                    }

                    val isSecurePayApp = method { name = "isSecurePayApp" }
                        .get(instance).boolean(activityInfo.applicationInfo.packageName)
                    if (isSecurePayApp) {
                        YLog.error("checkStartActivityForConfirm isSecurePayApp return")
//                        return@after
                    }

                    val getUserId = UserHandleClass.method { name = "getUserId" }.get().int(
                        activityInfo.applicationInfo.uid
                    )
                    val intent = method { name = "getCheckConformIntent" }.get(instance)
                        .invoke<Intent>(
                            callerPkg, activityInfo, sourceIntent,
                            realCallingUid, requestCode,
                            sourceRecord, getUserId, activityOptions
                        )
                    if (intent != null) {
                        YLog.info("checkStartActivityForConfirm getCheckConformIntent ok")

                        val mTaskSupervisor = activityTaskManagerService.current().field {
                            type = "com.android.server.wm.ActivityTaskSupervisor"
                            if (activityTaskManagerService.javaClass.hasField {
                                    type = "com.android.server.wm.ActivityTaskSupervisor"
                                }.not()) superClass()
                        }.any() ?: return@after

                        val mAmInternal = activityTaskManagerService.current().field {
                            type = "android.app.ActivityManagerInternal"
                            if (activityTaskManagerService.javaClass.hasField {
                                    type = "android.app.ActivityManagerInternal"
                                }.not()) superClass()
                        }.any() ?: return@after

                        val getCurrentUserId =
                            mAmInternal.current().method { name = "getCurrentUserId" }.int()

                        YLog.info("checkStartActivityForConfirm resolveActivity params ok")

                        val info = mTaskSupervisor.current().method {
                            name = "resolveActivity";paramCount = 7
                        }.invoke<ActivityInfo>(
                            intent, null, 0, profilerInfo,
                            getCurrentUserId, realCallingUid, Binder.getCallingUid()
                        )

                        if (info != null) {
                            YLog.info("checkStartActivityForConfirm resolveActivity ok")

                            result = Pair(intent, info)
                            YLog.info("checkStartActivityForConfirm result pair ok")

                        }

                        field { name = "mHasConformActivity" }.get(instance).setFalse()
                    }

                    YLog.info("checkStartActivityForConfirm after stop")

                }
            }
            method { name = "isSecurePayApp" }.hook {
//                replaceToFalse()
            }
        }

        //Source ScanPackageUtils
//        findClass("com.android.server.pm.ScanPackageUtils").hook {
//            injectMember {
//                method { name = "assertMinSignatureSchemeIsValid";paramCount(2) }
//                beforeHook {
//                    val clazz = "com.android.server.pm.pkg.parsing.ParsingPackageUtils"
//                        .toClassOrNull()
//                    val isSystemDir = clazz?.field { name = "PARSE_IS_SYSTEM_DIR";type(IntType) }
//                        ?.get()?.cast<Int>() ?: return@beforeHook
//                    val parseFlags = args().last().cast<Int>() ?: return@beforeHook
//                    if ((parseFlags and isSystemDir) != 0) resultNull()
//                }
//            }
//        }

        //Source ApkSignatureVerifier
//        findClass("android.util.apk.ApkSignatureVerifier").hook {
//            injectMember {
//                method { name = "unsafeGetCertsWithoutVerification";paramCount(3) }
//                beforeHook {
//                    val clazz = "android.content.pm.SigningDetails\$SignatureSchemeVersion"
//                        .toClassOrNull()
//                    val jar = clazz?.field { name = "JAR";type(IntType) }?.get()?.cast<Int>()
//                        ?: return@beforeHook
//                    args().last().set(jar)
//                }
//            }
//        }

        //电源菜单显示延迟
        //loadHooker(ReducePowerMenuDisplayDelay)

        //OPLUS_FEATURE_POWERKEY_SHORT_PRESS_SHUTDOWN = "oplus.software.short_press_powerkey_shutdown";
        //OPLUS_FEATURE_POWERKEY_SHUTDOWN = "oplus.software.long_press_powerkey_shutdown";
    }
}
