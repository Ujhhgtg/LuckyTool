package com.luckyzyx.luckytool.hook.scope.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class HookFeatureConfigManager(private val features: Map<String, Any>) : YukiBaseHooker() {
    override fun onHook() {
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