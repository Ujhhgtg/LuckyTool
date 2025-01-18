package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object FixInstallButtonDisplayException : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfusedButton
        "com.android.packageinstaller.oplus.view.ConfusedButton".toClass().apply {
            method { name = "getText" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                }
            }
        }
    }
}