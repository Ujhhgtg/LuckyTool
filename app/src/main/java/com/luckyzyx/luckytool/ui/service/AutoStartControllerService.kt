package com.luckyzyx.luckytool.ui.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.drake.net.utils.scope
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.NotifyUtils
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ShellUtils
import com.luckyzyx.luckytool.utils.getBoolean
import kotlinx.coroutines.Dispatchers

@Obfuscate
class AutoStartControllerService : Service() {
    @SuppressLint("InlinedApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "auto_start_channel"
        val channelNotifyId = 1001
        val channelName = getString(R.string.auto_start_service_channel_name)
        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_LOW
        )
        NotifyUtils.createChannel(this, channel)

        scope(Dispatchers.Default) {
            val notify = NotificationCompat.Builder(
                this@AutoStartControllerService, channelId
            ).apply {
                setAutoCancel(false)
                setOngoing(true)
                setSmallIcon(R.mipmap.ic_launcher_round)
                setContentTitle(getString(R.string.auto_start_service_channel_title))
                priority = NotificationCompat.PRIORITY_LOW
            }.build()
            if (SDK >= A14) startForeground(
                channelNotifyId, notify,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
            ) else startForeground(channelNotifyId, notify)

            val bundle = intent?.extras ?: Bundle()
            val command = ArrayList<String>()
            //FPS自启
            if (bundle.getBoolean("fps_auto", false)) {
                val fpsMode = bundle.getInt("fps_mode", 1)
                val fpsCur = bundle.getInt("fps_cur", -1)
                if ((fpsMode == 2) && (fpsCur != -1)) {
                    command.add("service call SurfaceFlinger 1035 i32 $fpsCur")
                }
            }
            //磁贴自启
            if (getBoolean(SettingsPrefs, "tile_auto_start", false)) {
                //触控采样率相关
                if (getBoolean(SettingsPrefs, "touch_sampling_rate", false)) {
                    command.add("echo > /proc/touchpanel/game_switch_enable 1")
                }
                //高亮度模式
                if (getBoolean(SettingsPrefs, "high_brightness_mode", false)) {
                    command.add("echo > /sys/kernel/oplus_display/hbm 1")
                }
                //全局DC模式
                if (getBoolean(SettingsPrefs, "global_dc_mode", false)) {
                    command.add("echo > /sys/kernel/oppo_display/dimlayer_hbm 1")
                    command.add("echo > /sys/kernel/oplus_display/dimlayer_hbm 1")
                }
            }
            if (command.isNotEmpty()) ShellUtils.execCommand(command, true)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}