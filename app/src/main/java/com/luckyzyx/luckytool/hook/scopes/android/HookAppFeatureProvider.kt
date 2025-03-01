package com.luckyzyx.luckytool.hook.scopes.android

import android.database.Cursor
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
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
//                addMethod { paramTypes(ContentResolverClass, null, StringClass) }
                addMethod { paramTypes(ContentResolverClass, null) }
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
                    paramTypes(ContentResolverClass, StringClass)
                    returnType(BooleanType)
                }
            }.apply {
                if (!isFeatureSupport) isFeatureSupport = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().apply {
                        method {
                            name = single().methodName
                            param(ContentResolverClass, StringClass)
                            returnType = BooleanType
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
                    paramTypes(ContentResolverClass, null, StringClass)
                    returnType(BooleanType)
                }
            }.apply {
                if (!isFeatureSupport) isFeatureSupport = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().apply {
                        method {
                            name = single().methodName
                            param(ContentResolverClass, VagueType, StringClass)
                            returnType = BooleanType
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
                    paramTypes(ContentResolverClass, StringClass, BooleanType)
                    returnType(BooleanType)
                }
            }.apply {
                isGetBoolean = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().apply {
                        method {
                            name = single().methodName
                            param(ContentResolverClass, StringClass, BooleanType)
                            returnType = BooleanType
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
                    paramTypes(ContentResolverClass, StringClass, StringClass)
                    returnType(StringClass)
                }
            }.apply {
                isGetString = singleOrNull() != null
                singleOrNull()?.let {
                    it.className.toClass().apply {
                        method {
                            name = single().methodName
                            param(ContentResolverClass, StringClass, StringClass)
                            returnType = StringClass
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