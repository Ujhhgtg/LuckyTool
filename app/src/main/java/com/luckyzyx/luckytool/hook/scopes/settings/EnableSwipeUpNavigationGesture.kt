package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableSwipeUpNavigationGesture : YukiBaseHooker() {
    override fun onHook() {
        //Source NavBarSettingsValueUtil
        "com.oplus.settings.feature.navbar.NavBarSettingsValueUtil".toClass().apply {
            method {
                name = "getGestureUpModeAvailable"
                param(ContextClass)
                returnType = IntType
            }.hook {
                replaceTo(0)
            }
        }
    }
}