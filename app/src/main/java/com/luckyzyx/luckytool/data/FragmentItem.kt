package com.luckyzyx.luckytool.data

import androidx.preference.PreferenceFragmentCompat
import java.io.Serializable

data class FragmentItem(
    val fragment: PreferenceFragmentCompat,
    val fragmentId: Int? = -1,
    val allPrefsItem: ArrayList<PrefsItem>
) : Serializable
