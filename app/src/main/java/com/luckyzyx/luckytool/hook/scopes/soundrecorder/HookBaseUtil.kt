package com.luckyzyx.luckytool.hook.scopes.soundrecorder

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
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