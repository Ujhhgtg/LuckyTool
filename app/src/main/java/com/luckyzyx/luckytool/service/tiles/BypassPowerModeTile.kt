package com.luckyzyx.luckytool.service.tiles

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITileServiceController
import com.luckyzyx.luckytool.service.TilesService
import com.luckyzyx.luckytool.utils.LogUtils

@Obfuscate
@Suppress("PrivatePropertyName")
class BypassPowerModeTile : TileService() {
    private val TAG = "BypassPowerModeTile"
    private var controller: ITileServiceController? = null

    private val batteryManager by lazy {
        getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Intent.ACTION_BATTERY_CHANGED) return
            if (controller != null && controller!!.checkBypassMode() && !batteryManager.isCharging) {
                controller?.bypassMode = false
                unregister()
                refreshData()
            }
        }
    }

    override fun onStartListening() {
        TilesService.get(this) {
            controller = it
            refreshData()
        }
    }

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                if (batteryManager.isCharging) {
                    register()
                    controller?.bypassMode = true
                }
            }

            Tile.STATE_ACTIVE -> {
                unregister()
                controller?.bypassMode = false
            }

            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun register() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    receiver,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                    Context.RECEIVER_EXPORTED and Context.RECEIVER_VISIBLE_TO_INSTANT_APPS
                )
            } else {
                registerReceiver(
                    receiver,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                )
            }
        } catch (e: Throwable) {
            LogUtils.e(TAG, "registerReceiver", "$e", true)
        }
    }

    private fun unregister() {
        try {
            unregisterReceiver(receiver)
        } catch (e: Throwable) {
            LogUtils.e(TAG, "unregisterReceiver", "$e", true)
        }
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkBypassMode()) Tile.STATE_UNAVAILABLE
        else if (controller!!.bypassMode) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
