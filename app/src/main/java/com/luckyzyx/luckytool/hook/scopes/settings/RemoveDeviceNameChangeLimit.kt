package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveDeviceNameChangeLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusDeviceInfoUtils -> PhoneNameSettingsActivity
        "com.oplus.settings.utils.OplusDeviceInfoUtils".toClass().apply {
            method { name = "getOplusVerifyDeviceNameSwitchState" }.hook {
                replaceToFalse()
            }
        }
    }
}