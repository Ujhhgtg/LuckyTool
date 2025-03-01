package com.luckyzyx.luckytool.data

import androidx.preference.PreferenceFragmentCompat
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class FragmentItem(
    val fragment: PreferenceFragmentCompat,
    val fragmentId: Int? = -1,
    val allPrefsItem: ArrayList<PrefsItem>
) : Serializable
