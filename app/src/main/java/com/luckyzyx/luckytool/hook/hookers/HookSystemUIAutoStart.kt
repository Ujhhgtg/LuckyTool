package com.luckyzyx.luckytool.hook.hookers

import android.content.Intent
import com.drake.net.utils.scope
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.BuildConfig
import kotlinx.coroutines.delay

object HookSystemUIAutoStart : YukiBaseHooker() {
    override fun onHook() {
        onAppLifecycle {
            //监听锁屏解锁
            registerReceiver(Intent.ACTION_USER_PRESENT) { context, _ ->
                scope {
                    delay(200)
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