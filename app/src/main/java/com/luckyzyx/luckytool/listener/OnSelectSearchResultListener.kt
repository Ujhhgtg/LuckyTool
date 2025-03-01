package com.luckyzyx.luckytool.listener

import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.data.FragmentItem
import com.luckyzyx.luckytool.data.PrefsItem

@Obfuscate
interface OnSelectSearchResultListener {
    fun resultItem(fragmentItem: FragmentItem, prefsItem: PrefsItem)
}