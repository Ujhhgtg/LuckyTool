package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
object RemoveControlCenterTileCountLimit : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) loadHooker(RemoveLimitNumberHint)
        else loadHooker(RemoveLimitNumberHintV14)
        loadHooker(RemoveReceiveItemLimit)
    }

    @Obfuscate
    object RemoveLimitNumberHint : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusSeparateQSCustomizer
            "com.oplus.systemui.plugins.qs.customize.OplusSeparateQSCustomizer".toClass().apply {
                method { name = "handleCheckLimitCount" }.hook {
                    replaceToFalse()
                }
                method { name = "updateLimitCountTip" }.hook {
                    intercept()
                }
            }
        }
    }

    @Obfuscate
    object RemoveReceiveItemLimit : YukiBaseHooker() {
        override fun onHook() {
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                //Source OplusQSCustomizer
                dexKitBridge.findClass {
                    matcher {
                        className(
                            "com.oplus.systemui.qs.customize.OplusQSCustomizer",
                            StringMatchType.Contains
                        )
                        addMethod { name("canReceiveItem");returnType(BooleanType) }
                        addMethod { name("checkHighLightTileSize");returnType(BooleanType) }
                    }
                }.apply {
                    checkDataList("RemoveReceiveItemLimit clazz", onlyOne = false)
                    forEachIndexed { _, classData ->
                        classData.name.toClass().apply {
                            method { name = "canReceiveItem" }.hook {
                                replaceToTrue()
                            }
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    object RemoveLimitNumberHintV14 : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusQSCustomizer
            "com.oplus.systemui.qs.customize.OplusQSCustomizer".toClass().apply {
                method { name = "handleCheckMinCount" }.hook {
                    replaceToFalse()
                }
                method { name = "showMinCountHint" }.hook {
                    intercept()
                }
            }
        }
    }
}