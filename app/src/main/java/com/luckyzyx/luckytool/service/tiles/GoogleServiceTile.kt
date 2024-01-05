package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGoogleServiceController
import com.luckyzyx.luckytool.service.controller.GoogleServiceControllerService
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
class GoogleServiceTile : TileService() {
    private var controller: IGoogleServiceController? = null

    override fun onStartListening() = startController()

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> controller?.googleStatus = true
            Tile.STATE_ACTIVE -> controller?.googleStatus = false
            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(GoogleServiceControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IGoogleServiceController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (controller!!.googleStatus) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}