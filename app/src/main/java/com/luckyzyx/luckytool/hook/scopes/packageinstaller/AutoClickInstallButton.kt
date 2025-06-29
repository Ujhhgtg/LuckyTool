package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.widget.Button
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoClickInstallButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OPlusPackageInstallerActivity
        "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity".toClass().resolve()
            .apply {
                firstMethod { name = "startInstallConfirm" }.hook {
                    after {
                        firstField { name = "mOk" }.of(instance).get<Button>()?.performClick()
                    }
                }
            }
        //Source InstallAppProgress
        "com.android.packageinstaller.oplus.InstallAppProgress".toClass().resolve().apply {
            firstMethod { name = "onPackageInstalled";parameterCount = 1 }.hook {
                after {
                    if (args().first().int() == 0) {
                        firstField { name = "mDoneButton" }.of(instance).get<Button>()
                            ?.performClick()
                    }
                }
            }
        }
    }
}