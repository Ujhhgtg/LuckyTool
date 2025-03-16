package com.luckyzyx.selector.listener

import com.luckyzyx.commonutils.data.AppInfo
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
interface OnSelectAppInfoListener {
    fun resultSelectAppInfos(list: ArrayList<AppInfo>)
}