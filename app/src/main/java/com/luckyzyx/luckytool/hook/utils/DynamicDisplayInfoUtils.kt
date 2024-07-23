package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.current
import com.luckyzyx.luckytool.data.DisplayMode

class DynamicDisplayInfoUtils(private val dynamicInfo: Any) {

    fun getSupportedDisplayModes(): Array<Any> {
        return dynamicInfo.current().field {
            name = "supportedDisplayModes"
        }.array<Any>()
    }

    fun getDisplayMode(modeIns: Any): Pair<Int, DisplayMode>? {
        val id = modeIns.current().field { name = "id" }.cast<Int>() ?: return null
        val width = modeIns.current().field { name = "width" }.cast<Int>()
        val height = modeIns.current().field { name = "height" }.cast<Int>()
        val xDpi = modeIns.current().field { name = "xDpi" }.cast<Float>()
        val yDpi = modeIns.current().field { name = "yDpi" }.cast<Float>()
        val refreshRate = modeIns.current().field {
            name {
                //C15 peakRefreshRate
                it.contains("refreshRate", true)
            }
        }.cast<Float>()
        val mode = DisplayMode(id, width, height, xDpi, yDpi, refreshRate)
        return Pair(id, mode)
    }
}