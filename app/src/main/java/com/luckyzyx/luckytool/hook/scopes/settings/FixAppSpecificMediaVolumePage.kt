package com.luckyzyx.luckytool.hook.scopes.settings

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.safeOfNull
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

class FixAppSpecificMediaVolumePage(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source EffectiveCompositionFactory
        dexKitBridge.findClass {
            matcher {
                className("com.oplus.anim", StringMatchType.StartsWith)
            }
        }.findMethod {
            matcher {
                paramTypes(ContextClass, StringClass, StringClass)
                usingStrings(".zip", ".lottie")
            }
        }.apply {
            checkDataList("FixAppSpecificMediaVolumePage")
            single().className.toClass().apply {
                method {
                    //fromAssetSync
                    name = single().methodName
                    param(ContextClass, StringClass, StringClass)
                }.hook {
                    before {
                        val context = args().first().cast<Context>() ?: return@before
                        val path = args(1).string()
                        val key = args().last().string()
                        if (path.contains("multi_app_volume").not()) return@before

                        val assetsInputStream = safeOfNull { context.assets.open(path) }
                        if (assetsInputStream != null) return@before

                        context.injectModuleAppResources()
                        if (!path.endsWith(".zip") && !path.endsWith(".lottie")) {
                            val resName = path.substringAfter("/").substringBefore(".json")
                            val resId = context.resources.getIdentifier(
                                resName, "raw", BuildConfig.APPLICATION_ID
                            )
                            if (resId == 0) return@before
                            val rawInputStream = safeOfNull {
                                context.resources.openRawResource(resId)
                            } ?: return@before
                            result = method {
//                                name = "fromJsonInputStreamSync"
                                param(InputStreamClass, StringClass)
                                returnType = method.returnType
                            }.get().call(rawInputStream, key) ?: return@before
                        }
                    }
                }
            }
        }
    }
}