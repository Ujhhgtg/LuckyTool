package com.luckyzyx.selector.listener

import com.luckyzyx.luckytool.data.FragmentItem
import com.luckyzyx.luckytool.data.PrefsItem
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
interface OnSelectSearchResultListener {
    fun resultItem(fragmentItem: FragmentItem, prefsItem: PrefsItem)
}