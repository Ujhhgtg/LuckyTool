package com.luckyzyx.luckytool.listener

import com.joom.paranoid.Obfuscate

@Obfuscate
interface OnSortChipListener {
    fun onReverseChange(isReverse: Boolean)
    fun onSortModeChange(sortMode: Int)
}