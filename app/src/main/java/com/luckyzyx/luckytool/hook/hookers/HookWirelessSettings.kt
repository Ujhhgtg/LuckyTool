package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.wirelesssettings.EnableWifiDetailsDisplayGateway
import com.luckyzyx.luckytool.utils.DexkitUtils
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookWirelessSettings : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //启用WiFi详情显示网关
            loadHooker(EnableWifiDetailsDisplayGateway(dexKitBridge))
        }

    }
}