package com.luckyzyx.luckytool.hook.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.StatusBarBatteryInfoNotify
import com.luckyzyx.luckytool.hook.scopes.systemui.StatusBarBatteryView
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.SDK
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class StatusBarBattery(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //电池图标
        loadHooker(StatusBarBatteryView(dexKitBridge))

        //电池信息通知
        if (SDK >= A12) loadHooker(StatusBarBatteryInfoNotify)
    }
}