package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookFeatureConfigManager(private val features: Map<String, Boolean>) : YukiBaseHooker() {
    override fun onHook() {
        if (features.isEmpty()) return
        //Source OplusFeatureConfigManager
        "com.oplus.content.OplusFeatureConfigManager".toClassOrNull()?.apply {
            method { name = "hasFeature";paramCount = 1 }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    if (features[key] != null) result = features[key]
                }
            }
        }
    }
}