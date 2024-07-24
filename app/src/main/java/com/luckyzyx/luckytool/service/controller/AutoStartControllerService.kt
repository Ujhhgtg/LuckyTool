package com.luckyzyx.luckytool.service.controller

import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.drake.net.utils.scope
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsCur
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyGlobalDCMode
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyHighBrightness
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTileAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRate
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRateLevel
import com.luckyzyx.luckytool.utils.NotifyUtils
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getInt
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.showToast
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.Dispatchers
import okhttp3.internal.toHexString

@Obfuscate
class AutoStartControllerService : Service() {

    private val channelId = "auto_start_channel"
    private val channelNotifyId = 1001
    private lateinit var channelName: String

    private lateinit var channel: NotificationChannel
    private lateinit var notify: Notification

    override fun onCreate() {
        channelName = getString(R.string.auto_start_service_channel_name)
        channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notify = NotificationCompat.Builder(this, channelId).apply {
            setAutoCancel(false)
            setOngoing(true)
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(getString(R.string.auto_start_service_channel_title))
            priority = NotificationCompat.PRIORITY_LOW
        }.build()
        NotifyUtils.createChannel(this@AutoStartControllerService, channel)
    }

    @SuppressLint("WrongConstant", "InlinedApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope(Dispatchers.Default) {
            try {
                if (SDK >= A14) startForeground(
                    channelNotifyId, notify, ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
                ) else startForeground(channelNotifyId, notify)
            } catch (e: ForegroundServiceStartNotAllowedException) {
                showToast(getString(R.string.service_auto_start_controller_not_allow_tips))
                return@scope
            } catch (e: Exception) {
                showToast("AutoStartControllerService cannot be started!")
                return@scope
            }

            val command = ArrayList<String>()
            //FPS自启
            if (getBoolean(SettingsPrefs, keyFpsAutoStart, false)) {
                val fpsCur = getInt(SettingsPrefs, keyFpsCur, -1)
                if (fpsCur != -1) command.add("service call SurfaceFlinger 1035 i32 $fpsCur")
            }
            //磁贴自启
            if (getBoolean(SettingsPrefs, keyTileAutoStart, false)) {
                //触控采样率相关
                if (getBoolean(SettingsPrefs, keyTouchSamplingRate, false)) {
                    val level = getString(SettingsPrefs, keyTouchSamplingRateLevel, "240")
                    val int16 = level.toInt().toHexString()
                    command.add("echo > /proc/touchpanel/game_switch_enable $int16")
//                    command.add("start touchDaemon && ps -A | grep touchDaemon")
                    command.add("touchHidlTest -c wo 0 26 $int16")
                }
                //高亮度模式
                if (getBoolean(SettingsPrefs, keyHighBrightness, false)) {
                    command.add("echo > /sys/kernel/oplus_display/hbm 1")
                }
                //全局DC模式
                if (getBoolean(SettingsPrefs, keyGlobalDCMode, false)) {
                    command.add("echo > /sys/kernel/oppo_display/dimlayer_hbm 1")
                    command.add("echo > /sys/kernel/oplus_display/dimlayer_hbm 1")
                }
            }
            if (command.isNotEmpty()) ShellUtils.fastCmd(*command.toTypedArray())
            scope(dispatcher = Dispatchers.Default) {
                AppAnalyticsUtils(this@AutoStartControllerService).checkAppForbiddenList()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}