package com.luckyzyx.luckytool.service.tiles

import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.closeCollapse
import com.luckyzyx.luckytool.utils.jumpBatteryInfo

@Obfuscate
class ChargingTestTile : TileService() {
    override fun onClick() {
        closeCollapse()
        jumpBatteryInfo(this)
    }
}