package com.luckyzyx.luckytool.listener

import android.content.pm.ResolveInfo
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
interface OnSelectResolveInfoListener {
    fun resultSelectResolveInfos(list: ArrayList<ResolveInfo>)
}