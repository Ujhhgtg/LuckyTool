package com.luckyzyx.luckytool.data

import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class DisplayMode(
    val id: Int,
    val width: Int? = null,
    val height: Int? = null,
    val xDpi: Float? = null,
    val yDpi: Float? = null,
    val refreshRate: Float? = null,
    val appVsyncOffsetNanos: Long? = null,
    val presentationDeadlineNanos: Long? = null,
    val group: Int? = null,
) : Serializable