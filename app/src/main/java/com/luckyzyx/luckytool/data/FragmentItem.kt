package com.luckyzyx.luckytool.data

import androidx.preference.PreferenceFragmentCompat
import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class FragmentItem(
    val fragment: PreferenceFragmentCompat,
    val fragmentId: Int? = -1,
    val title: CharSequence? = "",
    val allPrefsItem: ArrayList<PrefsItem>
) : Serializable
