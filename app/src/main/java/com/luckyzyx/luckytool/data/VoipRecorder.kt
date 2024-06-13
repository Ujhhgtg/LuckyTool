package com.luckyzyx.luckytool.data

import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class VoipRecorder(
    val packName: String,
    val appName: String,
    val activity: String = ""
) : Serializable
