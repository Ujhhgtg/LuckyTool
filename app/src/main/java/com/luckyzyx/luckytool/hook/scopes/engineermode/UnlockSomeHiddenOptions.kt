package com.luckyzyx.luckytool.hook.scopes.engineermode

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object UnlockSomeHiddenOptions : YukiBaseHooker() {
    override fun onHook() {
        //Source SecrecyServiceHelper
        "com.oplus.engineermode.impl.SecrecyServiceHelper".toClass().resolve().apply {
            firstMethod { name = "isSecrecySupported" }.hook {
                replaceToTrue()
            }
            firstMethod { name = "getSecrecyState" }.hook {
                replaceToFalse()
            }
        }
    }
}