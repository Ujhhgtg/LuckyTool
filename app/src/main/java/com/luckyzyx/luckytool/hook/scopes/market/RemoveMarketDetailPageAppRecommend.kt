package com.luckyzyx.luckytool.hook.scopes.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.allFields
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveMarketDetailPageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //com.heytap.cdo.client.detail.app.AppDetailActivity
        //com.heytap.cdo.client.detail.app.base.ScrollContentView

        "com.heytap.cdo.client.detail.app.AppDetailActivity".toClass().apply {
            method { param(IntentClass) }.hook {
                after {
                    val resourceDto = result<Any>()
                    if (resourceDto == null) {
                        YLog.debug("resourceDto is null")
                        return@after
                    }

                    "com.heytap.cdo.common.domain.dto.ResourceDto".toClass().allFields { index, field ->
                        val ff = field.get(resourceDto)
                        YLog.debug("$index | ${field.name} -> ${ff}")
                    }

                }
            }
        }
    }
}