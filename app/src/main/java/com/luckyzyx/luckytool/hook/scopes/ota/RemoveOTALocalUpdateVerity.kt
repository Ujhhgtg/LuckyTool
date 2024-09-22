package com.luckyzyx.luckytool.hook.scopes.ota

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.FileClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveOTALocalUpdateVerity(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source VerifyOTAPackageUtil
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(IntType)
                    addForType(FileClass)
                    addForType(ListClass)
                    addForType(MapClass)
                }
                methods {
                    add { paramCount(0);returnType(IntType) }
                    add { paramCount(0);returnType(FileClass) }
                    add { paramCount(0);returnType(ListClass) }
                }
                usingStrings("META-INF/com/android/metadata")
            }
        }.apply {
            checkDataList("RemoveOTALocalUpdateVerity", isDebug = true)
            single().name.toClass().apply {
                method { emptyParam();returnType = ListClass }.hook {
                    after {
                        result<ArrayList<String>>()?.removeIf { it.contains("forbid_ota_local_update") }
                    }
                }
            }
        }
    }
}