package com.luckyzyx.luckytool.listener

import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.data.AppInfo

@Obfuscate
interface OnResultSelectAppListener {
    /**
     * 返回选中AppInfo列表
     * @param list ArrayList<AppInfo>
     */
    fun resultSelectAppInfos(list: ArrayList<AppInfo>)
}