package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IHighBrightnessController
import com.luckyzyx.luckytool.service.controller.HighBrightnessControllerService
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyHighBrightness
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.putBoolean

@Obfuscate
class HighBrightnessModeTile : TileService() {
    private var controller: IHighBrightnessController? = null
    override fun onStartListening() = startController()

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                controller?.highBrightnessMode = true
                putBoolean(SettingsPrefs, keyHighBrightness, true)
                dataChannel("com.android.systemui").put(keyHighBrightness, true)
            }

            Tile.STATE_ACTIVE -> {
                controller?.highBrightnessMode = false
                putBoolean(SettingsPrefs, keyHighBrightness, false)
                dataChannel("com.android.systemui").put(keyHighBrightness, false)
            }

            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            HighBrightnessControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IHighBrightnessController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkHighBrightnessMode()) Tile.STATE_UNAVAILABLE
        else if (controller!!.highBrightnessMode) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
        if (qsTile.state == Tile.STATE_UNAVAILABLE) putBoolean(
            SettingsPrefs, keyHighBrightness, false
        )
    }
}
