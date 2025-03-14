package com.luckyzyx.luckytool.hook.scopes.launcher

import android.content.pm.LauncherApps
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveWidgetsAddRequestWhitelist : YukiBaseHooker() {
    override fun onHook() {
        //Source WidgetControlHelper
        "com.android.launcher3.widget.WidgetControlHelper".toClassOrNull() ?: return
        //Source AddItemActivity
        "com.android.launcher3.dragndrop.AddItemActivity".toClass().apply {
            method {
                name = "isAllowedAddWidget"
                param(ContextClass, LauncherApps.PinItemRequest::class.java)
            }.hook {
                replaceToTrue()
            }
        }
    }
}