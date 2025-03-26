package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import java.security.SecureRandom

@Obfuscate
object FixInstallButtonDisplayException : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfusedButton
        "com.android.packageinstaller.oplus.view.ConfusedButton".toClass().apply {
            method { name = "getAccessibilityViewId" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                    field { name = "mRandom" }.get(instance).set(SecureRandom())
                }
            }
            method { name = "getText" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                    field { name = "mRandom" }.get(instance).set(SecureRandom())
                }
            }
        }
        //Source ConfusedTextView
        "com.android.packageinstaller.oplus.view.ConfusedTextView".toClass().apply {
            method { name = "getAccessibilityViewId" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                    field { name = "mRandom" }.get(instance).set(SecureRandom())
                }
            }
            method { name = "getText" }.hook {
                before {
                    field { name = "mIsCtsTesting" }.get(instance).setTrue()
                    field { name = "mRandom" }.get(instance).set(SecureRandom())
                }
            }
        }
    }
}