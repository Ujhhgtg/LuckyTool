package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.provider.Settings
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.SDK

object ForceEnableScreenOffMusicSupport : YukiBaseHooker() {
    override fun onHook() {
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
                    val utilCls =
                        if (SDK >= A14) "com.oplus.systemui.aod.mediapanel.util.AodMediaStatisticUtil"
                        else "com.oplusos.systemui.notification.util.NotificationStatisticUtil"
                    utilCls.toClass().method { name = "setAodMediaSupport";paramCount = 1 }.get()
                        .call(true)
                }
            }
        }
    }
}