package com.luckyzyx.luckytool.hook.scopes.android

import android.content.ContentResolver
import android.database.Cursor
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookAppFeatureProvider(
    val dexKitBridge: DexKitBridge, private val features: Map<String, Any>
) : YukiBaseHooker() {

    private var isFeatureSupport = false
    private var isGetBoolean = false
    private var isGetString = false

    override fun onHook() {
        if (features.isEmpty()) return

        //Source AppFeatureProviderUtils
        dexKitBridge.findClass {
            matcher {
//                addFieldForType(Uri::class.java)
//                addMethod { paramTypes(ContentResolverClass, null, String::class.java) }
                addMethod { paramTypes(ContentResolver::class.java, null) }
                addMethod {
                    usingStrings("featurename")
                    returnType(Cursor::class.java)
                }
                usingStrings(
//                    "AppFeatureProviderUtils",
                    "content://com.oplus.customize.coreapp.configmanager.configprovider.AppFeatureProvider"
                )
            }
        }.apply {
            checkDataList("AppFeatureProviderUtils [$packageName]")
            findMethod {
                matcher {
//                    name("isFeatureSupport")
                    paramTypes(ContentResolver::class.java, String::class.java)
                    returnType(Boolean::class.java)
                }
            }.apply {
                if (!isFeatureSupport) isFeatureSupport = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(ContentResolver::class.java, String::class.java)
                            returnType = Boolean::class.java
                        }.hook {
                            before {
                                val key = args().last().cast<String>()
                                if (key.isNullOrBlank()) return@before
                                val value = features[key]
                                if (value != null && value is Boolean) result = value
                            }
                        }
                    }
                }
            }
            findMethod {
                matcher {
//                    name("isFeatureSupport")
                    paramTypes(ContentResolver::class.java, null, String::class.java)
                    returnType(Boolean::class.java)
                }
            }.apply {
                if (!isFeatureSupport) isFeatureSupport = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(ContentResolver::class.java, VagueType, String::class.java)
                            returnType = Boolean::class.java
                        }.hook {
                            before {
                                val key = args().last().cast<String>()
                                if (key.isNullOrBlank()) return@before
                                val value = features[key]
                                if (value != null && value is Boolean) result = value
                            }
                        }
                    }
                }
            }
            if (!isFeatureSupport) {
                YLog.debug("AppFeatureProviderUtils [$packageName] -> isFeatureSupport is null")
            }

            findMethod {
                matcher {
//                    name("getBoolean")
                    paramTypes(ContentResolver::class.java, String::class.java, Boolean::class.java)
                    returnType(Boolean::class.java)
                }
            }.apply {
                isGetBoolean = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(
                                ContentResolver::class.java,
                                String::class.java,
                                Boolean::class.java
                            )
                            returnType = Boolean::class.java
                        }.hook {
                            before {
                                val key = args(1).cast<String>()
                                if (key.isNullOrBlank()) return@before
                                val value = features[key]
                                if (value != null && value is Boolean) result = value
                            }
                        }
                    }
                }
            }

            findMethod {
                matcher {
//                    name("getString")
                    paramTypes(ContentResolver::class.java, String::class.java, String::class.java)
                    returnType(String::class.java)
                }
            }.apply {
                isGetString = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(
                                ContentResolver::class.java,
                                String::class.java,
                                String::class.java
                            )
                            returnType = String::class.java
                        }.hook {
                            before {
                                val key = args(1).cast<String>()
                                if (key.isNullOrBlank()) return@before
                                val value = features[key]
                                if (value != null && value is String) result = value
                            }
                        }
                    }
                }
            }
        }
    }
}