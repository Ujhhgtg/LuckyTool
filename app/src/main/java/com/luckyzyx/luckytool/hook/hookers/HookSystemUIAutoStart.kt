package com.luckyzyx.luckytool.hook.hookers

import android.content.Intent
import com.drake.net.utils.scope
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsCur
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsMode
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.setRefresh
import kotlinx.coroutines.delay

object HookSystemUIAutoStart : YukiBaseHooker() {
    override fun onHook() {
        val fpsList = arrayOf("30.0", "60.0", "90.0", "120.0", "144.0")

        var fpsAutoStart = prefs(SettingsPrefs).getBoolean(keyFpsAutoStart, false)
        dataChannel.wait<Boolean>(keyFpsAutoStart) { fpsAutoStart = it }
        var fpsMode = prefs(SettingsPrefs).getInt(keyFpsMode, 1)
        dataChannel.wait<Int>(keyFpsMode) { fpsMode = it }
        var currentFps = prefs(SettingsPrefs).getInt(keyFpsCur, -1)
        dataChannel.wait<Int>(keyFpsCur) { currentFps = it }

        onAppLifecycle {
            //监听锁屏解锁
            registerReceiver(Intent.ACTION_USER_PRESENT) { context, _ ->
                scope {
                    delay(200)
                    if (fpsAutoStart && (fpsMode == 1) && (currentFps != -1)) {
                        setRefresh(context, fpsList[currentFps], fpsList[currentFps])
                    }
                    context.startForegroundService(Intent().apply {
                        action = "${BuildConfig.APPLICATION_ID}.AutoStartControllerService"
                        setPackage(BuildConfig.APPLICATION_ID)
                    })
                }.catch {
                    YLog.debug("AutoStartService throw", it)
                }
            }
        }
    }
}