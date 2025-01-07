package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object FixInstallButtonDisplayException : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfusedButton
        "com.android.packageinstaller.oplus.view.ConfusedButton".toClass().apply {
            method { name = "setCts" }.hook {
                before {
                    args().first().setTrue()
                }
            }
        }
    }
}