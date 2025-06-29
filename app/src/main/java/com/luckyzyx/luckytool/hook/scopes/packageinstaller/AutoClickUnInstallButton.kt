package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.widget.Button
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoClickUnInstallButton : YukiBaseHooker() {
    override fun onHook() {
        //Source UninstallerActivity
        "com.android.packageinstaller.UninstallerActivity".toClass().resolve().apply {
            firstMethod { name = "showUninstallConfirmation";parameterCount = 1 }.hook {
                after {
                    firstField { name = "mUnInstallButton" }.of(instance).get<Button>()?.performClick()
                }
            }
        }
        //Source InstallAppProgress
        "com.android.packageinstaller.oplus.OPlusUninstallAppProgress".toClass().resolve().apply {
            firstMethod { name = "initView" }.hook {
                after {
                    firstField { name = "mOkButton" }.of(instance).get<Button>()?.performClick()
                }
            }
        }
    }
}