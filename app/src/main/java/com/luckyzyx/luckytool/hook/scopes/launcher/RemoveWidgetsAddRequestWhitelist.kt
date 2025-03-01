package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveWidgetsAddRequestWhitelist : YukiBaseHooker() {
    override fun onHook() {
        //Source AddItemActivity
        "com.android.launcher3.dragndrop.AddItemActivity".toClass().apply {
            method { name = "isAllowedAddWidget" }.hook{
                replaceToTrue()
            }
        }
    }
}