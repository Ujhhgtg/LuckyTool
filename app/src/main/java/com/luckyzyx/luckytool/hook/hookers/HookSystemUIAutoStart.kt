package com.luckyzyx.luckytool.hook.hookers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.drake.net.utils.scope
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import kotlinx.coroutines.delay

@Obfuscate
object HookSystemUIAutoStart : YukiBaseHooker() {
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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

            onCreate {
                val intentFilter = IntentFilter("LuckyTool_CloseCollapse")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(object : BroadcastReceiver() {
                        @SuppressLint("WrongConstant")
                        override fun onReceive(context: Context?, intent: Intent?) {
                            val service = context?.getSystemService(Context.STATUS_BAR_SERVICE)
                                ?: return
                            service.javaClass.method { name = "collapsePanels" }.get(service).call()
                        }
                    }, intentFilter, Context.RECEIVER_EXPORTED)
                } else {
                    registerReceiver(object : BroadcastReceiver() {
                        @SuppressLint("WrongConstant", "InlinedApi")
                        override fun onReceive(context: Context?, intent: Intent?) {
                            val service = context?.getSystemService(Context.STATUS_BAR_SERVICE)
                                ?: return
                            service.javaClass.method { name = "collapsePanels" }.get(service).call()
                        }
                    }, intentFilter)
                }
            }
        }
    }
}