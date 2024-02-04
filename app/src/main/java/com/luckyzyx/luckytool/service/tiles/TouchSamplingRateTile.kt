package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITouchPanelController
import com.luckyzyx.luckytool.service.controller.TouchPanelControllerService
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRate
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRateLevel
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putBoolean

@Obfuscate
class TouchSamplingRateTile : TileService() {
    private var controller: ITouchPanelController? = null
    override fun onStartListening() = startController()

    override fun onClick() {
        val level = getString(SettingsPrefs, keyTouchSamplingRateLevel, "0")
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                controller?.touchMode = level?.toInt() ?: 0
                putBoolean(SettingsPrefs, keyTouchSamplingRate, true)
            }

            Tile.STATE_ACTIVE -> {
                controller?.touchMode = 0
                putBoolean(SettingsPrefs, keyTouchSamplingRate, false)
            }

            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            TouchPanelControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = ITouchPanelController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkTouchMode()) Tile.STATE_UNAVAILABLE
        else if (controller!!.touchMode > 0) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
        if (qsTile.state == Tile.STATE_UNAVAILABLE) putBoolean(
            SettingsPrefs, keyTouchSamplingRate, false
        )
    }
}
