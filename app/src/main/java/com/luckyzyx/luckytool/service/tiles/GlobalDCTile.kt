package com.luckyzyx.luckytool.service.tiles

import android.content.ComponentName
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGlobalDCController
import com.luckyzyx.luckytool.service.controller.GlobalDCControllerService
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyGlobalDCMode
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.putBoolean

@Obfuscate
class GlobalDCTile : TileService() {
    private var controller: IGlobalDCController? = null

    override fun onStartListening() = startController()

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                controller?.globalDCMode = true
                putBoolean(SettingsPrefs, keyGlobalDCMode, true)
                dataChannel("com.android.systemui").put(keyGlobalDCMode, true)
            }

            Tile.STATE_ACTIVE -> {
                controller?.globalDCMode = false
                putBoolean(SettingsPrefs, keyGlobalDCMode, false)
                dataChannel("com.android.systemui").put(keyGlobalDCMode, false)
            }

            Tile.STATE_UNAVAILABLE -> {}
        }
        refreshData()
    }

    private fun startController() {
        if (controller == null) bindRootService(
            GlobalDCControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IGlobalDCController.Stub.asInterface(iBinder)
                refreshData()
            })
    }

    private fun refreshData() {
        qsTile.state = if (controller == null) Tile.STATE_UNAVAILABLE
        else if (!controller!!.checkGlobalDCMode()) Tile.STATE_UNAVAILABLE
        else if (controller!!.globalDCMode) Tile.STATE_ACTIVE
        else Tile.STATE_INACTIVE
        qsTile.updateTile()
        if (qsTile.state == Tile.STATE_UNAVAILABLE) putBoolean(
            SettingsPrefs, keyGlobalDCMode, false
        )
    }
}
