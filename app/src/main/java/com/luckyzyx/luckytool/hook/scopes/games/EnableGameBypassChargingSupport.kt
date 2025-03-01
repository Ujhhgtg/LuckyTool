package com.luckyzyx.luckytool.hook.scopes.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableGameBypassChargingSupport : YukiBaseHooker() {
    override fun onHook() {
        //Source COSAExportedImpl
        "com.oplus.cosa.exported.COSAExportedImpl".toClass().apply {
            method { name = "getBypassChargingDeviceSupport" }.hook{
                replaceTo(2)
            }
        }
    }
}