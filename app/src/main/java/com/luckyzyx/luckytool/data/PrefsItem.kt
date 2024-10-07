package com.luckyzyx.luckytool.data

import android.graphics.drawable.Drawable
import androidx.preference.Preference
import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class PrefsItem(
    val preference: Preference,
    val position: Int,
    val key: String? = "",
    val icon: Drawable? = null,
    val title: CharSequence? = "",
    val summary: CharSequence? = "",
    val isVisible: Boolean? = true,
    val fragmentId: Int? = -1
) : Serializable
