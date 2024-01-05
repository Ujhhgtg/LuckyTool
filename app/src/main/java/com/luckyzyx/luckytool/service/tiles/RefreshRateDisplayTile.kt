package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.service.controller.RefreshRateControllerService
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
class RefreshRateDisplayTile : TileService() {
    private var controller: IRefreshRateController? = null

    override fun onStartListening() = startController()

    override fun onClick() {
        if (qsTile.state == Tile.STATE_INACTIVE) controller?.refreshRateDisplay = true
        else if (qsTile.state == Tile.STATE_ACTIVE) controller?.refreshRateDisplay = false
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            RefreshRateControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IRefreshRateController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (controller!!.refreshRateDisplay) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
