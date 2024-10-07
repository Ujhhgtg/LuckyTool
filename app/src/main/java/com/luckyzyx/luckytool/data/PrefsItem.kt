package com.luckyzyx.luckytool.data

import android.graphics.drawable.Drawable
import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class PrefsItem(
    val key: String? = "",
    val icon: Drawable? = null,
    val title: CharSequence? = "",
    val summary: CharSequence? = "",
    val isVisible: Boolean? = true,
    val fragmentId: Int? = -1
) : Serializable
