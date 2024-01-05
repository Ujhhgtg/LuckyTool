package com.luckyzyx.luckytool.service.tiles

import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.closeCollapse
import com.luckyzyx.luckytool.utils.jumpRunningApp

@Obfuscate
class ProcessManagerTile : TileService() {
    override fun onClick() {
        closeCollapse()
        jumpRunningApp(this)
    }
}