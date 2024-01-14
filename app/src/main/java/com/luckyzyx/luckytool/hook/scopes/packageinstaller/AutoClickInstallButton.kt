package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.widget.Button
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

object AutoClickInstallButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OPlusPackageInstallerActivity
        "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity".toClass().apply {
            method { name = "startInstallConfirm" }.hook {
                after {
                    field { name = "mOk" }.get(instance).cast<Button>()?.performClick()
                }
            }
        }
        //Source InstallAppProgress
        "com.android.packageinstaller.oplus.InstallAppProgress".toClass().apply {
            method { name = "onPackageInstalled";paramCount = 1 }.hook {
                after {
                    if (args().first().int() == 0) field { name = "mDoneButton" }.get(instance)
                        .cast<Button>()?.performClick()
                }
            }
        }
    }
}