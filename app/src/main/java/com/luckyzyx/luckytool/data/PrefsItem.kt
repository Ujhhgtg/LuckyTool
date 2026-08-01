package com.luckyzyx.luckytool.data

import android.graphics.drawable.Drawable
import androidx.preference.Preference
import java.io.Serializable

data class PrefsItem(
    val preference: Preference,
    val position: Int,
    val key: String? = "",
    val icon: Drawable? = null,
    val title: CharSequence? = "",
    val summary: CharSequence? = "",
    val isVisible: Boolean? = true,
    val fragmentTitle: CharSequence? = "",
    val fragmentSummary: CharSequence? = "",
    val fragmentResId: Int? = -1
) : Serializable
