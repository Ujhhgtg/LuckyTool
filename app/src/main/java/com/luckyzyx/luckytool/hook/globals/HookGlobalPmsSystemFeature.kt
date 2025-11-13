package com.luckyzyx.luckytool.hook.globals

import android.util.ArrayMap
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookGlobalPmsSystemFeature : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        val list = ArrayMap<String, Boolean>().apply {
            //Source OplusRateLimitClassfier AppTypeClassfier initFeature
            //Source CredentialManagerService
            //Source OplusClearDataProtectManager interceptClearUserDataIfNeeded
            //Source PackageManagerServiceExtImpl shouldRemoveUpdatedMainlineApk
            if (prefs(ModulePrefs).getBoolean("remove_gms_usage_restrictions", false)) {
                put("cn.google.services", false)
            }
        }
        loadHooker(PmSystemFeature(list))
        if (packageName == "android") {
            loadHooker(PmsSystemFeature(list))
        }
    }

    @Obfuscate
    class PmSystemFeature(private val features: Map<String, Boolean>) : YukiBaseHooker() {
        override fun onHook() {
            //Source ApplicationPackageManager
            "android.app.ApplicationPackageManager".toClass().resolve().apply {
                firstMethod {
                    name = "hasSystemFeature"
                    parameters(String::class)
                    returnType = Boolean::class
                }.hook {
                    after {
                        val key = args().first().cast<String>()
                        if (key.isNullOrBlank()) return@after
                        if (features[key] != null) result = features[key]
                    }
                }
            }
        }
    }

    @Obfuscate
    class PmsSystemFeature(private val features: Map<String, Boolean>) : YukiBaseHooker() {
        override fun onHook() {
            //Source PackageManagerService
            "com.android.server.pm.PackageManagerService".toClass().resolve().apply {
                firstMethod {
                    name = "hasSystemFeature"
                    parameters(String::class, Int::class)
                    returnType = Boolean::class
                }.hook {
                    after {
                        val key = args().first().cast<String>()
                        if (key.isNullOrBlank()) return@after
                        if (features[key] != null) result = features[key]
                    }
                }
            }
        }
    }
}