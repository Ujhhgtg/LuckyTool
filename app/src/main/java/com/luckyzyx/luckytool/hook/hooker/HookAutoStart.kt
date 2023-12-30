package com.luckyzyx.luckytool.hook.hooker

import android.content.Intent
import com.drake.net.utils.scope
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.setRefresh
import kotlinx.coroutines.delay

object HookAutoStart : YukiBaseHooker() {
    override fun onHook() {
        val fpsList = arrayOf("30.0", "60.0", "90.0", "120.0", "144.0")

        var fpsAutoStart = prefs(SettingsPrefs).getBoolean("fps_autostart", false)
        dataChannel.wait<Boolean>("fps_autostart") { fpsAutoStart = it }
        var fpsMode = prefs(SettingsPrefs).getInt("fps_mode", 1)
        dataChannel.wait<Int>("fps_mode") { fpsMode = it }
        var currentFps = prefs(SettingsPrefs).getInt("current_fps", -1)
        dataChannel.wait<Int>("current_fps") { currentFps = it }

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