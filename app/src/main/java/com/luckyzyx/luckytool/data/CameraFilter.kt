package com.luckyzyx.luckytool.data

import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class CameraFilter(
    val key: String,
    val title: String,
    var isEnable: Boolean = false
) : Serializable
