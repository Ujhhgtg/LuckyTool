package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.joom.paranoid.Obfuscate

@Obfuscate
object HideInActiveSignalLabelsGen2x2 : YukiBaseHooker() {
    override fun onHook() {
        //Source MobileIconSets -> Companion -> config_isSystemUiExpSignalUi
        VariousClass(
            "com.oplus.systemui.statusbar.policy.MobileIconSets", //C13 C15
            "com.oplusos.systemui.statusbar.policy.MobileIconSets" //C14
        ).toClass(initialize = true).apply {
            if (hasField { name = "VOLTE_ICON" } && hasField { name = "VOLTE_ICON_EX" }) {
                val volteIconEx = field { name = "VOLTE_ICON_EX" }.get().cast<IntArray>() ?: return
                field { name = "VOLTE_ICON" }.get().set(volteIconEx)
            }
        }
    }
}