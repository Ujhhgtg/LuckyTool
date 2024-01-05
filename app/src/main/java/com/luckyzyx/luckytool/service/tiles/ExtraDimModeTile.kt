package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IDarkModeController
import com.luckyzyx.luckytool.service.controller.DarkModeControllerService
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
class ExtraDimModeTile : TileService() {
    private var controller: IDarkModeController? = null
    override fun onStartListening() = startController()

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> controller?.darkMode = true
            Tile.STATE_ACTIVE -> controller?.darkMode = false
            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            DarkModeControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IDarkModeController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkDarkMode()) Tile.STATE_UNAVAILABLE
        else if (controller!!.darkMode) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
