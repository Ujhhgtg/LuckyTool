package com.luckyzyx.luckytool.hook.utils

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.luckyzyx.luckytool.data.DisplayMode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class DynamicDisplayInfoUtils(private val dynamicInfo: Any) {

    fun getSupportedDisplayModes(): Array<Any> {
        return dynamicInfo.resolve().firstField {
            name = "supportedDisplayModes"
        }.get<Array<Any>>() ?: arrayOf()
    }

    fun getDisplayMode(modeIns: Any): Pair<Int, DisplayMode>? {
        val id = modeIns.resolve().firstField { name = "id" }.get<Int>() ?: return null
        val width = modeIns.resolve().firstField { name = "width" }.get<Int>()
        val height = modeIns.resolve().firstField { name = "height" }.get<Int>()
        val xDpi = modeIns.resolve().firstField { name = "xDpi" }.get<Float>()
        val yDpi = modeIns.resolve().firstField { name = "yDpi" }.get<Float>()
        val refreshRate = modeIns.resolve().firstField {
            name {
                //C15 peakRefreshRate
                it.contains("refreshRate", true)
            }
        }.get<Float>()
        val mode = DisplayMode(id, width, height, xDpi, yDpi, refreshRate)
        return Pair(id, mode)
    }
}