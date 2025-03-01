package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import java.security.SecureRandom

@Obfuscate
object FixInstallButtonDisplayException : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfusedButton
        "com.android.packageinstaller.oplus.view.ConfusedButton".toClass().apply {
            val hasRamdom = hasField { type = SecureRandom::class.java }
            if (hasRamdom) constructor().hookAll {
                after {
                    field { type = SecureRandom::class.java }.get(instance).set(SecureRandom())
                }
            }
            method { name = "getText" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                }
            }
        }
    }
}