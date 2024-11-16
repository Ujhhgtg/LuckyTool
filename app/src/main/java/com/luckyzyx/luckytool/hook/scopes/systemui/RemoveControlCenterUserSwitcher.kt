package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveControlCenterUserSwitcher : YukiBaseHooker() {
    override fun onHook() {
        //Search Log showUserSwitcher
        "com.oplusos.systemui.qs.OplusQSFooterImpl".toClass().apply {
            method {
                name = "showUserSwitcher"
                emptyParam()
                returnType = BooleanType
            }.hook {
                replaceToFalse()
            }
        }
    }
}