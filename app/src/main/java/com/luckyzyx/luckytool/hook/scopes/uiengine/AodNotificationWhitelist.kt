package com.luckyzyx.luckytool.hook.scopes.uiengine

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveAodNotificationWhitelist : YukiBaseHooker() {
    override fun onHook() {
        //Source NotificationView -> BaseView
        "com.oplus.egview.widget.BaseView".toClass().apply {
            method { name = "isExpRegion" }.hook {
                replaceToTrue()
            }
        }
    }
}