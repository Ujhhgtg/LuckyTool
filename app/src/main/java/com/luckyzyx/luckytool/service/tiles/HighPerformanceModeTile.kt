package com.luckyzyx.luckytool.service.tiles

import android.service.quicksettings.TileService
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.closeCollapse
import com.luckyzyx.luckytool.utils.jumpHighPerformance

@Obfuscate
class HighPerformanceModeTile : TileService() {
    override fun onClick() {
        closeCollapse()
        jumpHighPerformance(this)
    }
}