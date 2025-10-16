package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class SkipDetailApkScan(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        loadHooker(HookStartAppDetail(dexKitBridge))
        loadHooker(HookCheckToScanRisk(dexKitBridge))
    }

    @Obfuscate
    class HookStartAppDetail(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source AppDetailRedirectionUtils
            dexKitBridge.findClass {
                matcher {
                    usingStrings("AppDetailRedirectionUtils", "RemoteAppdetailService")
                }
            }.apply {
                checkDataList("AppDetailRedirectionUtils")
                findMethod {
                    matcher {
                        paramTypes(Context::class.java, String::class.java)
                        returnType(Int::class.java)
                        usingStrings("count_canceled_by_app_detail", "com.oplus.appdetail")
                    }
                }.apply {
                    checkDataList("checkCommon")
                    single().className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(Context::class, String::class)
                            returnType = Int::class
                        }.hook {
                            replaceTo(9)
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    class HookCheckToScanRisk(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source OPlusPackageInstallerActivity
            dexKitBridge.findClass {
                matcher {
                    className("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity")
                }
            }.apply {
                checkDataList("OPlusPackageInstallerActivity")
                val checkToScanRisk = findMethod {
                    matcher {
                        paramCount(0)
                        usingFields {
                            add { type(Long::class.java) }
                            add { type(Boolean::class.java) }
                            add { type(View::class.java) }
                        }
                        addCaller { name("onClick") }
                        addInvoke { paramCount(0);returnType(Void.TYPE) }
                        usingNumbers(8)
                    }
                }.apply {
                    checkDataList("checkToScanRisk")
                }.single()

                val initiateInstall = findMethod {
                    matcher {
                        paramCount(0)
                        usingFields {
                            add { type(PackageManager::class.java) }
                            add { type(PackageInfo::class.java) }
                            add { type(ApplicationInfo::class.java) }
                            add { type(Boolean::class.java) }
                        }
                        addCaller { name("onClick") }
                        addInvoke { paramCount(0);returnType(Void.TYPE) }
                        usingNumbers(3, 8388608, 8192)
                    }
                }.apply {
                    checkDataList("initiateInstall")
                }.single()

                single().name.toClass().resolve().apply {
                    firstMethod { name = checkToScanRisk.name }.hook {
                        before {
                            firstMethod { name = initiateInstall.name }.of(instance).invoke()
                            resultNull()
                        }
                    }
                }
            }
        }
    }
}
