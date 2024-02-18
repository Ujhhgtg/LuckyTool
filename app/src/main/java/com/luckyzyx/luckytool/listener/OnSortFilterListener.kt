package com.luckyzyx.luckytool.listener

import com.joom.paranoid.Obfuscate

@Obfuscate
interface OnSortFilterListener {
    fun onRefreshData()
    fun onReverseChange(isReverse: Boolean)
    fun onSortModeChange(sortMode: Int)
    fun onShowSystemChange(showSystem: Boolean)
}