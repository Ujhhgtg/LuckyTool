package com.luckyzyx.luckytool.listener

import com.luckyzyx.luckytool.data.FragmentItem
import com.luckyzyx.luckytool.data.PrefsItem

interface OnSelectSearchResultListener {
    fun resultItem(fragmentItem: FragmentItem, prefsItem: PrefsItem)
}