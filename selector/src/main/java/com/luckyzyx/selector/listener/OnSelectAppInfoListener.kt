package com.luckyzyx.selector.listener

import com.luckyzyx.luckytool.data.AppInfo
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
interface OnSelectAppInfoListener {
    fun resultSelectAppInfos(list: ArrayList<AppInfo>)
}