package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.joom.paranoid.Obfuscate

@Obfuscate
object DisableHighVolumeWarningNotifications : YukiBaseHooker() {
    override fun onHook() {
        val volumeReceiver = VariousClass(
            "com.oplusos.systemui.notification.receiver.VolumeReceiver", //C12 C13
            "com.oplus.systemui.statusbar.receiver.VolumeReceiver" //C14 C15
        ).toClass()

        //Source OplusPowerUI
        VariousClass(
            "com.oplusos.systemui.notification.power.OplusPowerUI", //C12 C13
            "com.oplus.systemui.statusbar.notification.power.OplusPowerUI" //C14 C15
        ).toClass().apply {
            method { name = "start" }.hook {
                after {
                    val context = field { type = ContextClass }.get(instance).cast<Context>()
                        ?: return@after
                    val mVolumeReceiver = field { type = volumeReceiver }.get(instance).any()
                        ?: return@after
                    mVolumeReceiver.current().method { name = "unregister";superClass() }
                        .call(context)
                }
            }
        }
    }
}