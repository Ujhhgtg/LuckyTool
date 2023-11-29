package com.luckyzyx.luckytool.hook.scope.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class HookAppFeatureProvider(
    val dexKitBridge: DexKitBridge, private val features: Map<String, Any>
) : YukiBaseHooker() {
    override fun onHook() {
        if (features.isEmpty()) return
        //Source AppFeatureProviderUtils
        dexKitBridge.findClass {
            matcher {
                methods {
                    add {
                        paramTypes(
                            ContentResolverClass, StringClass, BooleanType
                        )
                        returnType(BooleanType)
                    }
                    add {
                        paramTypes(
                            ContentResolverClass, StringClass, StringClass
                        )
                        returnType(StringClass)
                    }
                    add {
                        paramTypes(ContentResolverClass, StringClass)
                        returnType(BooleanType)
                    }
                }
                usingStrings("AppFeatureProviderUtils")
            }
        }.apply {
            checkDataList("AppFeatureProviderUtils ($packageName)")
            first().name.toClass().apply {
                method {
                    param(ContentResolverClass, StringClass)
                    returnType = BooleanType
                }.hook {
                    before {
                        val key = args(1).cast<String>()
                        if (key.isNullOrBlank()) return@before
                        val value = features[key]
                        if (value != null && value is Boolean) result = value
                    }
                }
                method {
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
                method {
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