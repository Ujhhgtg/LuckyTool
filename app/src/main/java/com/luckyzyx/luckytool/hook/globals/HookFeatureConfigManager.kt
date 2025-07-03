package com.luckyzyx.luckytool.hook.globals

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookFeatureConfigManager(private val features: Map<String, Boolean>) : YukiBaseHooker() {
    override fun onHook() {
        if (features.isEmpty()) return
        //Source OplusFeatureConfigManager
        "com.oplus.content.OplusFeatureConfigManager".toClassOrNull()?.resolve()?.optional()?.apply {
            firstMethod {
                name = "hasFeature"
                parameterCount = 1
            }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    if (features[key] != null) result = features[key]
                }
            }
        }
    }
}