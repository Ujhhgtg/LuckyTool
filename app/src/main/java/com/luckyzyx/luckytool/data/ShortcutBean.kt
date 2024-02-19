package com.luckyzyx.luckytool.data

import android.content.Intent
import android.graphics.drawable.Icon
import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class ShortcutBean(
    val label: String,
    val key: String,
    val icon: Icon? = null,
    var intent: Intent? = null,
    var isEnable: Boolean = false
) : Serializable