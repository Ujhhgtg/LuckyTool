package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.telephony.SubscriptionManager
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IFiveGController
import com.luckyzyx.luckytool.service.controller.FiveGControllerService
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
class FiveGTile : TileService() {
    private var controller: IFiveGController? = null
    override fun onStartListening() = startController()

    override fun onClick() {
        val subId = SubscriptionManager.getDefaultDataSubscriptionId()
        if (qsTile.state == Tile.STATE_INACTIVE) controller?.setFiveGStatus(subId, true)
        else if (qsTile.state == Tile.STATE_ACTIVE) controller?.setFiveGStatus(subId, false)
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            FiveGControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IFiveGController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        val subId = SubscriptionManager.getDefaultDataSubscriptionId()
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkCompatibility(subId)) Tile.STATE_UNAVAILABLE
        else if (controller!!.getFiveGStatus(subId)) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
