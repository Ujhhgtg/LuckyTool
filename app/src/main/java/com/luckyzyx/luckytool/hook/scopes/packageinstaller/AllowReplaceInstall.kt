package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AllowReplaceInstall : YukiBaseHooker() {
    override fun onHook() {
        //Search ->  currentVersionCode / apkVersioncode -> Method
        "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity".toClass().resolve().apply {
            firstMethod { name = "parseReplaceInstall" }.hook {
                before {
                    firstMethod { name = "preSafeInstall" }.of(instance).invoke()
                    resultNull()
                }
            }
        }
    }
}