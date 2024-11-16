package com.luckyzyx.luckytool.hook.scopes.ota

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FileClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveOTALocalUpdateVerity(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookOTAMetadata(dexKitBridge))
        loadHooker(HookPayloadProperties(dexKitBridge))
    }

    class HookOTAMetadata(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
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
                checkDataList("RemoveOTALocalUpdateVerity MetaData")
                single().name.toClass().apply {
                    method { emptyParam();returnType = ListClass }.hook {
                        after {
                            result<ArrayList<String>>()?.apply {
                                removeIf { it.contains("forbid_ota_local_update") }
                                removeIf { it.contains("from_version") }
                            }
                        }
                    }
                }
            }
        }
    }

    class HookPayloadProperties(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source ABUpdateManager
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(ContextClass)
                        addForType(IntType)
                        addForType(FloatType)
                        addForType(BooleanType)
                        addForType("android.os.UpdateEngine")
                        addForType("android.os.UpdateEngineCallback")
                    }
                    methods {
                        add { paramCount(0);returnType(IntType) }
                        add { paramCount(0);returnType(FloatType) }
                        add { paramCount(0);returnType(UnitType) }
                        add { paramTypes(StringClass, LongType, LongType, StringArrayClass) }
                    }
                    usingStrings("ABUpdateManager", "payload_properties")
                }
            }.apply {
                checkDataList("RemoveOTALocalUpdateVerity Properties")
                single().name.toClass().apply {
                    method { param(StringClass, LongType, LongType, StringArrayClass) }.hook {
                        after {
                            val headers = args().last().array<String>()
                            headers.toMutableList().apply {
                                removeIf { it.contains("forbid_ota_local_update") }
//                                removeIf { it.contains("oplus_update_engine_verify_disable") }
                            }
                        }
                    }
                }
            }
        }
    }
}