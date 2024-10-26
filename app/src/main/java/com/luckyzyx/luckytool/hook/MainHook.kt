package com.luckyzyx.luckytool.hook

import android.os.Build.VERSION_CODES.R
import android.os.Build.VERSION_CODES.S
import android.os.Build.VERSION_CODES.S_V2
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForR
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForS
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForT
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForU
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForV
import com.luckyzyx.luckytool.hook.hookers.HookAlarmClock
import com.luckyzyx.luckytool.hook.hookers.HookAndroid
import com.luckyzyx.luckytool.hook.hookers.HookAtlasService
import com.luckyzyx.luckytool.hook.hookers.HookAudioEffectCenter
import com.luckyzyx.luckytool.hook.hookers.HookAudioMonitor
import com.luckyzyx.luckytool.hook.hookers.HookBattery
import com.luckyzyx.luckytool.hook.hookers.HookBrowser
import com.luckyzyx.luckytool.hook.hookers.HookCalendar
import com.luckyzyx.luckytool.hook.hookers.HookCallUI
import com.luckyzyx.luckytool.hook.hookers.HookCamera
import com.luckyzyx.luckytool.hook.hookers.HookCloudService
import com.luckyzyx.luckytool.hook.hookers.HookCosa
import com.luckyzyx.luckytool.hook.hookers.HookDirectUI
import com.luckyzyx.luckytool.hook.hookers.HookExternalStorage
import com.luckyzyx.luckytool.hook.hookers.HookEyeProtect
import com.luckyzyx.luckytool.hook.hookers.HookGallery
import com.luckyzyx.luckytool.hook.hookers.HookGesture
import com.luckyzyx.luckytool.hook.hookers.HookKeyguardClock
import com.luckyzyx.luckytool.hook.hookers.HookLauncher
import com.luckyzyx.luckytool.hook.hookers.HookMarket
import com.luckyzyx.luckytool.hook.hookers.HookMediaController
import com.luckyzyx.luckytool.hook.hookers.HookMultiApp
import com.luckyzyx.luckytool.hook.hookers.HookNotificationManager
import com.luckyzyx.luckytool.hook.hookers.HookOplusGames
import com.luckyzyx.luckytool.hook.hookers.HookOplusMMS
import com.luckyzyx.luckytool.hook.hookers.HookOplusOta
import com.luckyzyx.luckytool.hook.hookers.HookOtherApp
import com.luckyzyx.luckytool.hook.hookers.HookPackageInstaller
import com.luckyzyx.luckytool.hook.hookers.HookPermissionController
import com.luckyzyx.luckytool.hook.hookers.HookPhone
import com.luckyzyx.luckytool.hook.hookers.HookPhoneManager
import com.luckyzyx.luckytool.hook.hookers.HookPictorial
import com.luckyzyx.luckytool.hook.hookers.HookQuickSearchBox
import com.luckyzyx.luckytool.hook.hookers.HookSafeCenter
import com.luckyzyx.luckytool.hook.hookers.HookScreenshot
import com.luckyzyx.luckytool.hook.hookers.HookSecurePay
import com.luckyzyx.luckytool.hook.hookers.HookSettings
import com.luckyzyx.luckytool.hook.hookers.HookSmartSidebar
import com.luckyzyx.luckytool.hook.hookers.HookSoundRecorder
import com.luckyzyx.luckytool.hook.hookers.HookSystemUI
import com.luckyzyx.luckytool.hook.hookers.HookThemeStore
import com.luckyzyx.luckytool.hook.hookers.HookUIEngine
import com.luckyzyx.luckytool.hook.hookers.HookWeather
import com.luckyzyx.luckytool.hook.scopes.android.DisableFlagSecure
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.SettingsPrefs
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object MainHook : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog {
            tag = "LuckyTool"
            isEnable = true
            isRecord = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = false
    }

    override fun onHook() = encase {
        if (prefs(ModulePrefs).getBoolean("enable_module", false).not()) return@encase
        if (prefs(SettingsPrefs).getBoolean("is_su", false).not()) return@encase

        //系统框架
        loadSystem(HookAndroid)

        //系统界面
        loadApp("com.android.systemui", HookSystemUI)

        //经典主题 Clock
        loadApp("com.oplus.keyguard.clock.base", HookKeyguardClock)

        //通知管理
        loadApp("com.oplus.notificationmanager", HookNotificationManager)

        //时钟
        loadApp("com.coloros.alarmclock", HookAlarmClock)
        //桌面
        loadApp("com.oppo.launcher", "com.android.launcher") {
            loadHooker(HookLauncher)
        }

        //息屏
//        loadApp("com.oplus.aod", HookAod)
        //百变引擎
        loadApp("com.oplus.uiengine", HookUIEngine)
        //截屏
        loadApp("com.oplus.screenshot", HookScreenshot)

        //安全中心
        loadApp("com.oplus.safecenter", "com.coloros.safecenter") {
            loadHooker(HookSafeCenter)
        }
        //应用安装器
        loadApp("com.android.packageinstaller", HookPackageInstaller)

        //外部存储设备
        loadApp("com.android.externalstorage", HookExternalStorage)

        //电池
        loadApp("com.oplus.battery", HookBattery)
        //设置
        loadApp("com.android.settings", HookSettings)
        //相机
        loadApp("com.oplus.camera", "com.oneplus.camera") {
            loadHooker(HookCamera)
        }
        //相册
        loadApp("com.coloros.gallery3d", HookGallery)
        //主题商店
        loadApp("com.heytap.themestore", "com.oplus.themestore") {
            loadHooker(HookThemeStore)
        }
        //云服务
        loadApp("com.heytap.cloud", HookCloudService)
        //游戏助手
        loadApp("com.oplus.games", HookOplusGames)
        //应用增强服务
        loadApp("com.oplus.cosa", HookCosa)
        //软件更新
        loadApp("com.oplus.ota", HookOplusOta)
        //乐划锁屏
        loadApp("com.heytap.pictorial", HookPictorial)
        //信息
        loadApp("com.android.mms", HookOplusMMS)
        //电话
        loadApp("com.android.incallui", HookCallUI)
        //电话服务
        loadApp("com.android.phone", HookPhone)
        //浏览器
        loadApp("com.heytap.browser", HookBrowser)
        //手势体感
        loadApp("com.oplus.gesture", HookGesture)
        //权限控制器
        loadApp("com.android.permissioncontroller", HookPermissionController)
        //小布识屏
        loadApp("com.coloros.directui", HookDirectUI)
        //全局搜索
        loadApp("com.heytap.quicksearchbox", HookQuickSearchBox)
        //软件商店
        loadApp("com.heytap.market", HookMarket)
        //天气
        loadApp("com.coloros.weather2", HookWeather)
        //日历
        loadApp("com.coloros.calendar", HookCalendar)
        //智能侧边栏
        loadApp("com.coloros.smartsidebar", HookSmartSidebar)
        //手机管家
        loadApp("com.coloros.phonemanager", HookPhoneManager)
        //支付保护
        loadApp("com.coloros.securepay", HookSecurePay)
        //应用分身
        loadApp("com.oplus.multiapp", HookMultiApp)
        //录音
        loadApp("com.coloros.soundrecorder", HookSoundRecorder)
        //三方应用通话录音 / 智慧语音
        loadApp("com.oplus.audiomonitor", HookAudioMonitor)
        //atlasService
        loadApp("com.oplus.atlas", HookAtlasService)
        //audioEffectCenter
        loadApp("com.oplus.audio.effectcenter", HookAudioEffectCenter)
        //护眼模式
        loadApp("com.oplus.eyeprotect", HookEyeProtect)
        //MediaController
        loadApp("com.oplus.mediacontroller", HookMediaController)

        //其他APP
        loadApp("com.ruet_cse_1503050.ragib.appbackup.pro", "ru.kslabs.ksweb", "com.dv.adm") {
            loadHooker(HookOtherApp)
        }
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
                if (lpparam.packageName == "android") {
                    if (lpparam.processName == "android") when (SDK) {
                        VANILLA_ICE_CREAM -> CorePatchForV().handleLoadPackage(lpparam)
                        UPSIDE_DOWN_CAKE -> CorePatchForU().handleLoadPackage(lpparam)
                        TIRAMISU -> CorePatchForT().handleLoadPackage(lpparam)
                        S, S_V2 -> CorePatchForS().handleLoadPackage(lpparam)
                        R -> CorePatchForR().handleLoadPackage(lpparam)
                        else -> YLog.error("[CorePatch] Unsupported Version of Android -> $SDK")
                    }
                }
                DisableFlagSecure().handleLoadPackage(lpparam)
            }
        }
        YukiXposedEvent.onInitZygote { startupParam: IXposedHookZygoteInit.StartupParam ->
            run {
                when (SDK) {
//                    VANILLA_ICE_CREAM -> CorePatchForV().initZygote(startupParam)
                    UPSIDE_DOWN_CAKE -> CorePatchForU().initZygote(startupParam)
                    TIRAMISU -> CorePatchForT().initZygote(startupParam)
                    S, S_V2 -> CorePatchForS().initZygote(startupParam)
                    R -> CorePatchForR().initZygote(startupParam)
                }
            }
        }
    }
}