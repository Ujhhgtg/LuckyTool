package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveUSBConnectDialog : YukiBaseHooker() {
    override fun onHook() {
        //Source UsbService
        VariousClass(
            "com.coloros.systemui.notification.usb.UsbService", //A11
            "com.oplusos.systemui.notification.usb.UsbService",
            "com.oplus.systemui.usb.UsbService" //C14 C15
        ).toClass().apply {
            val hasUsbConnected = hasMethod { name = "onUsbConnected" }
            method {
                name {
                    if (hasUsbConnected) it == "onUsbConnected"
                    else it.contains("onUsbConnected")
                }
            }.hook {
                replaceUnit {
                    val ins = if (hasUsbConnected) instance else args().first().any()
                    val context = args().last().cast<Context>() ?: return@replaceUnit
                    method { name = "onUsbSelect" }.get(ins).call(1)
                    method { name = "updateAdbNotification" }.get(ins).call(context)
                    method { name = "updateUsbNotification" }.get(ins).call(context, 1)
                    method { name = "changeUsbConfig" }.get(ins).call(context, 1)
                }
            }
            method { name = "updateUsbNotification" }.hook {
                before {
                    field {
                        name { it.contains("NeedShowUsbDialog", true) }
                    }.get(instance).setFalse()
                }
            }
        }
    }
}