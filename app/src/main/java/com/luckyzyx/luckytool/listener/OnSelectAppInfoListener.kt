package com.luckyzyx.luckytool.listener

import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.data.AppInfo

@Obfuscate
interface OnSelectAppInfoListener {
    fun resultSelectAppInfos(list: ArrayList<AppInfo>)
}