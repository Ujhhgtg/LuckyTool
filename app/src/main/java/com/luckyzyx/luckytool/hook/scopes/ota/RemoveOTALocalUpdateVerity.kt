package com.luckyzyx.luckytool.hook.scopes.ota

import android.content.Context
import android.os.SystemProperties
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.ArrayClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import java.io.File

@Obfuscate
class RemoveOTALocalUpdateVerity(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookOTAMetadata(dexKitBridge))
        loadHooker(HookPayloadProperties(dexKitBridge))
    }

    @Obfuscate
    class HookOTAMetadata(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source VerifyOTAPackageUtil
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(Int::class.java)
                        addForType(File::class.java)
                        addForType(List::class.java)
                        addForType(Map::class.java)
                    }
                    methods {
                        add { paramCount(0);returnType(Int::class.java) }
                        add { paramCount(0);returnType(File::class.java) }
                        add { paramCount(0);returnType(List::class.java) }
                    }
                    usingStrings("META-INF/com/android/metadata")
                }
            }.apply {
                checkDataList("RemoveOTALocalUpdateVerity MetaData")
                single().name.toClass().resolve().apply {
                    firstMethod {
                        emptyParameters()
                        returnType = List::class
                    }.hook {
                        after {
                            result<ArrayList<String>>()?.apply {
                                removeIf { it.contains("forbid_ota_local_update") }
                                removeIf { it.contains("from_version") }
                            }
                            SystemProperties.set("sys.ota.grant_ota_local_update", "true")
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    class HookPayloadProperties(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source ABUpdateManager
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(Context::class.java)
                        addForType(Int::class.java)
                        addForType(Float::class.java)
                        addForType(Boolean::class.java)
                        addForType("android.os.UpdateEngine")
                        addForType("android.os.UpdateEngineCallback")
                    }
                    methods {
                        add { paramCount(0);returnType(Int::class.java) }
                        add { paramCount(0);returnType(Float::class.java) }
                        add { paramCount(0);returnType(Void.TYPE) }
                        add {
                            paramTypes(
                                String::class.java, Long::class.java,
                                Long::class.java, ArrayClass(String::class)
                            )
                        }
                    }
                    usingStrings("ABUpdateManager", "payload_properties")
                }
            }.apply {
                checkDataList("RemoveOTALocalUpdateVerity Properties")
                single().name.toClass().resolve().apply {
                    firstMethod {
                        parameters(
                            String::class, Long::class,
                            Long::class, ArrayClass(String::class)
                        )
                    }.hook {
                        before {
                            val headers = args().last().array<String>()
                            headers.toMutableList().apply {
                                removeIf { it.contains("forbid_ota_local_update") }
//                                removeIf { it.contains("oplus_update_engine_verify_disable") }
                            }
                            SystemProperties.set("sys.ota.grant_ota_local_update", "true")
                        }
                    }
                }
            }
        }
    }
}