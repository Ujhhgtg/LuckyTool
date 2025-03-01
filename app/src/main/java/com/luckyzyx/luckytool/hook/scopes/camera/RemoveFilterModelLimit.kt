package com.luckyzyx.luckytool.hook.scopes.camera

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveFilterModelLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source SystemUtil
        "com.oplus.ocs.camera.ipusdk.processunit.filter.list.SystemUtil".toClass().apply {
            method { name = "isMarketNameContainSeriesNum" }.hook {
                replaceToTrue()
            }
        }
    }
}