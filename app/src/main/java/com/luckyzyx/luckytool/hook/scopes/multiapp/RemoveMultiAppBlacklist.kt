package com.luckyzyx.luckytool.hook.scopes.multiapp

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveMultiAppBlacklist : YukiBaseHooker() {
    override fun onHook() {
        //Source MultiAppBlackListUpdateHelper
        "com.oplus.multiapp.utils.MultiAppBlackListUpdateHelper".toClass().apply {
            method { name = "loadMultiappBlackListConfig" }.hook {
                intercept()
            }
        }
    }
}