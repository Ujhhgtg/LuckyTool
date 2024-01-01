package com.luckyzyx.luckytool.hook.scope.soundrecorder

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object HookBaseUtil : YukiBaseHooker() {
    override fun onHook() {
        //Source BaseUtil
        "com.soundrecorder.base.utils.BaseUtil".toClass().apply {
            method { name = "isRealme" }.hook {
                replaceToTrue()
            }
        }
    }
}