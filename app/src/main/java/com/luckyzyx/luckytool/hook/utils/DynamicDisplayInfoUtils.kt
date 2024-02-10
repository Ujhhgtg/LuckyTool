package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.current
import com.luckyzyx.luckytool.utils.DisplayMode

class DynamicDisplayInfoUtils(private val dynamicInfo: Any) {

    fun getSupportedDisplayModes(): Array<Any> {
        return dynamicInfo.current().field {
            name = "supportedDisplayModes"
        }.array<Any>()
    }

    fun getDisplayMode(modeIns: Any): Triple<Int, DisplayMode, Nothing?>? {
        val id = modeIns.current().field { name = "id" }.cast<Int>() ?: return null
        val width = modeIns.current().field { name = "width" }.cast<Int>()
        val height = modeIns.current().field { name = "height" }.cast<Int>()
        val xDpi = modeIns.current().field { name = "xDpi" }.cast<Float>()
        val yDpi = modeIns.current().field { name = "yDpi" }.cast<Float>()
        val refreshRate = modeIns.current().field { name = "refreshRate" }.cast<Float>()
        val appVsyncOffsetNanos = modeIns.current().field {
            name = "appVsyncOffsetNanos"
        }.cast<Long>()
        val presentationDeadlineNanos = modeIns.current().field {
            name = "presentationDeadlineNanos"
        }.cast<Long>()
        val group = modeIns.current().field { name = "group" }.cast<Int>()
        val mode = DisplayMode(
            id, width, height, xDpi, yDpi,
            refreshRate, appVsyncOffsetNanos, presentationDeadlineNanos, group
        )
        return Triple(id, mode, null)
    }
}