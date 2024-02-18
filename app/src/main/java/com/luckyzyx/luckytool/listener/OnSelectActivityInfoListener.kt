package com.luckyzyx.luckytool.listener

import android.content.pm.ActivityInfo
import com.joom.paranoid.Obfuscate

@Obfuscate
interface OnSelectActivityInfoListener {
    fun resultSelectActivityInfos(list: ArrayList<ActivityInfo>)
}