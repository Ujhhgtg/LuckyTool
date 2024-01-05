package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITouchPanelController
import com.luckyzyx.luckytool.service.controller.TouchPanelControllerService
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRate
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.putBoolean

@Obfuscate
class TouchSamplingRateTile : TileService() {
    private var controller: ITouchPanelController? = null
    override fun onStartListening() = startController()

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                controller?.touchMode = true
                putBoolean(SettingsPrefs, keyTouchSamplingRate, true)
                dataChannel("com.android.systemui").put(keyTouchSamplingRate, true)
            }

            Tile.STATE_ACTIVE -> {
                controller?.touchMode = false
                putBoolean(SettingsPrefs, keyTouchSamplingRate, false)
                dataChannel("com.android.systemui").put(keyTouchSamplingRate, false)
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
        else if (controller!!.touchMode) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
        if (qsTile.state == Tile.STATE_UNAVAILABLE) putBoolean(
            SettingsPrefs, keyTouchSamplingRate, false
        )
    }
}
