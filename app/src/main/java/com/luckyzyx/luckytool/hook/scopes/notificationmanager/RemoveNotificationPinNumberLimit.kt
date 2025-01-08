package com.luckyzyx.luckytool.hook.scopes.notificationmanager

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveNotificationPinNumberLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source AppNotificationTopController
        "com.oplus.notificationmanager.property.uicontroller.AppNotificationTopController".toClass()
            .apply {
                method {
                    param(this@apply, "androidx.preference.Preference", AnyClass)
                    returnType = BooleanType
                }.hookAll {
                    before {
                        val ins = args().first().any() ?: return@before
                        val bool = args().last().boolean()
                        method { name = "onChange";superClass() }.get(ins).call(bool)
                        resultTrue()
                    }
                }
            }
    }
}