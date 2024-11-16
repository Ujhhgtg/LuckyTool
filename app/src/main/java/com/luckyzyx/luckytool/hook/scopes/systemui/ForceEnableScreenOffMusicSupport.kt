package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.provider.Settings
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceEnableScreenOffMusicSupport : YukiBaseHooker() {
    override fun onHook() {
        val statisticUtil = VariousClass(
            "com.oplusos.systemui.notification.util.NotificationStatisticUtil",  //C13
            "com.oplus.systemui.aod.mediapanel.util.AodMediaStatisticUtil"  //C14
        ).toClass()

        //Source OplusBlackScreenGestureControllExImpl
        VariousClass(
            "com.oplus.systemui.keyguard.OplusBlackScreenGestureControllExImpl", //C13
            "com.oplus.systemui.keyguard.gesture.OplusBlackScreenGestureControllExImpl" //C14
        ).toClass().apply {
            method { name = "resetAodMediaSupportConfig" }.hook {
                after {
                    val context = field { name = "mContext" }.get(instance).cast<Context>()
                        ?: return@after
                    Settings.Secure::class.java.method {
                        name = "putIntForUser";paramCount = 4
                    }.get().call(context.contentResolver, "aod_media_support", 1, 0)
                    statisticUtil.method { name = "setAodMediaSupport";paramCount = 1 }
                        .get().call(true)
                }
            }
        }
    }
}