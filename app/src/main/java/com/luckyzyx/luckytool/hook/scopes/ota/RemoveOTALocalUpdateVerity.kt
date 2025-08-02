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
import java.io.InputStream

@Obfuscate
class RemoveOTALocalUpdateVerity(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookABUpdateUtils(dexKitBridge))
        loadHooker(HookLocalPcakgeInfoUtil(dexKitBridge))
        loadHooker(HookPayloadProperties(dexKitBridge))
    }

    @Obfuscate
    class HookABUpdateUtils(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source ABUpdateUtils
            dexKitBridge.findClass {
                matcher {
                    addFieldForType(java.util.ArrayList::class.java)
                    methods {
                        add { paramCount(0);returnType(Boolean::class.java) }
                        add {
                            addParamType(InputStream::class.java)
                            returnType(java.util.ArrayList::class.java)
                        }
                        add {
                            paramTypes(File::class.java, String::class.java)
                            returnType(java.util.ArrayList::class.java)
                        }
                        add {
                            paramTypes(List::class.java)
                            returnType(HashMap::class.java)
                        }
                    }
                    usingStrings("ABUpdateUtils", "META-INF/com/android/metadata")
                }
            }.apply {
                checkDataList("RemoveOTALocalUpdateVerity find ABUpdateUtils")
                single().name.toClass().resolve().apply {
                    firstMethod {
                        parameters(File::class, String::class)
                        returnType = ArrayList::class
                    }.hook {
                        after {
                            val file = args().first().cast<File>() ?: return@after
                            val list = result<java.util.ArrayList<String>>() ?: return@after

                            if (file.exists() && file.name.contains("downgrade")) {
                                list.removeIf { it.contains("forbid_ota_local_update") }
                                list.removeIf { it.contains("ota_root_or_debug") }
                            } else {
                                list[list.indexOfFirst { it.contains("forbid_ota_local_update") }] =
                                    "forbid_ota_local_update=false"
                                list[list.indexOfFirst { it.contains("ota_root_or_debug") }] =
                                    "ota_root_or_debug=false"
                                list.removeIf { it.contains("from_version") }
                            }
                            SystemProperties.set("sys.ota.grant_ota_local_update", "true")
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    class HookLocalPcakgeInfoUtil(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source LocalPcakgeInfoUtil
            dexKitBridge.findClass {
                matcher {
                    fieldCount(0)
                    methods {
                        add { returnType(Boolean::class.java) }
                        add {
                            paramTypes(String::class.java)
                            returnType(String::class.java)
                        }
                        add {
                            paramTypes(Context::class.java, String::class.java)
                            returnType(java.util.ArrayList::class.java)
                        }
                        add {
                            paramTypes(
                                java.util.ArrayList::class.java, Long::class.java,
                                Int::class.java, Context::class.java
                            )
                            returnType(Int::class.java)
                        }
                    }
                    usingStrings("LocalPcakgeInfoUtil")
                }
            }.apply {
                checkDataList("RemoveOTALocalUpdateVerity find LocalPcakgeInfoUtil")
                single().name.toClass().resolve().apply {
                    firstMethod {
                        parameters { it.contains(Context::class.java) && it.contains(String::class.java) }
                        returnType = ArrayList::class
                    }.hook {
                        after {
                            val filePath = args().last().string()
                            val list = result<java.util.ArrayList<String>>() ?: return@after

                            if (filePath.contains("downgrade")) {
                                list.removeIf { it.contains("forbid_ota_local_update") }
                                list.removeIf { it.contains("ota_root_or_debug") }
                            } else {
                                list[list.indexOfFirst { it.contains("forbid_ota_local_update") }] =
                                    "forbid_ota_local_update=false"
                                list[list.indexOfFirst { it.contains("ota_root_or_debug") }] =
                                    "ota_root_or_debug=false"
                                list.removeIf { it.contains("from_version") }
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