package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class AllowReplaceInstall(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source OPlusPackageInstallerActivity
        //Search ->  currentVersionCode / apkVersioncode -> Method
        dexKitBridge.findClass {
            matcher {
                className("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity")
            }
        }.apply {
            checkDataList("OPlusPackageInstallerActivity")
            val parseReplaceInstall = findMethod {
                matcher {
                    paramCount(0)
                    usingStrings("currentVersionCode", "apkVersioncode")
                }
            }.apply {
                checkDataList("parseReplaceInstall")
            }.single()

            val preSafeInstall = findMethod {
                matcher {
                    paramCount(0)
                    usingStrings("startAppdetail", "reason")
                }
            }.apply {
                checkDataList("parseReplaceInstall")
            }.single()

            single().name.toClass().resolve().apply {
                firstMethod {
                    name = parseReplaceInstall.name
                }.hook {
                    before {
                        firstMethod { name = preSafeInstall.name }.of(instance).invoke()
                        resultNull()
                    }
                }
            }
        }
    }
}