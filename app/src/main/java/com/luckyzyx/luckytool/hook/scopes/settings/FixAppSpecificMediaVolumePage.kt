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
import com.luckyzyx.luckytool.utils.safeOfNull

object FixAppSpecificMediaVolumePage : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source EffectiveCompositionFactory
        "com.oplus.anim.EffectiveCompositionFactory".toClass().apply {
            method {
                name = "fromAssetSync"
                param(ContextClass, StringClass, StringClass)
            }.hook {
                before {
                    val context = args().first().cast<Context>() ?: return@before
                    val path = args(1).string()
                    val key = args().last().string()
                    if (path.contains("multi_app_volume").not()) return@before
                    context.injectModuleAppResources()
                    if (!path.endsWith(".zip") && !path.endsWith(".lottie")) {
                        val resName = path.substringAfter("/").substringBefore(".json")
                        val resId = context.resources.getIdentifier(
                            resName, "raw", BuildConfig.APPLICATION_ID
                        )
                        if (resId == 0) return@before
                        val inputStream = safeOfNull { context.resources.openRawResource(resId) }
                            ?: return@before
                        result = method {
                            name = "fromJsonInputStreamSync"
                            param(InputStreamClass, StringClass)
                        }.get().call(inputStream, key) ?: return@before
                    }
                }
            }
        }
    }
}