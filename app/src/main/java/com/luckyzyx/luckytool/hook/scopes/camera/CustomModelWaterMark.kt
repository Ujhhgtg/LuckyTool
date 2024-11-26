package com.luckyzyx.luckytool.hook.scopes.camera

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class CustomModelWaterMark(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val waterMark = prefs(ModulePrefs).getString("custom_model_watermark", "None")
        if (waterMark.isBlank() || waterMark == "None") return

        //Source BaseWatermarkPresenter / BaseWatermarkCreator
        dexKitBridge.findMethod {
            matcher {
                paramTypes(ContextClass, FloatType, null, null)
                usingStrings(
                    "key_watermark_part_a_line", "key_watermark_part_b_line"
                )
                usingNumbers(0.03F, 0.007F)
            }
        }.apply {
            checkDataList("CustomModelWaterMark Shot")
            single().className.toClass().apply {
                method {
                    name(single().methodName)
                    param(ContextClass, FloatType, VagueType, VagueType)
                    returnType(single().returnTypeName)
                }.hook {
                    after {
                        val hashMap = result<HashMap<String, Any>>() ?: return@after
                        hashMap["key_watermark_part_a_line"]?.apply {
                            javaClass.field { type = ArrayListClass }.get(this)
                                .cast<ArrayList<String>>()?.apply {
                                    replaceAll { if (it.contains("Shot on OnePlus")) waterMark else it }
                                }
                        }
                        hashMap["key_watermark_part_b_line"]?.apply {
                            javaClass.field { type = ArrayListClass }.get(this)
                                .cast<ArrayList<String>>()?.apply {
                                    replaceAll { if (it.contains("Shot on OnePlus")) waterMark else it }
                                }
                        }
                    }
                }
            }
        }

        //Source MarketUtil / MarketNameInfo
        dexKitBridge.findMethod {
            matcher {
                paramCount(0)
                returnType(StringClass)
                usingStrings(
                    "", "ro.vendor.oplus.market.enname", "ro.vendor.oplus.market.name"
                )
            }
        }.apply {
            checkDataList("CustomModelWaterMark MarketName", false)
            forEach {
                it.className.toClass().apply {
                    method {
                        name = it.methodName
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        replaceTo(waterMark)
                    }
                }
            }
        }

        //Source WatermarkHelper / WatermarkSingleton
        dexKitBridge.findMethod {
            matcher {
                returnType(StringClass)
                usingStrings("[\u4e00-\u9fa5]", "")
            }
        }.apply {
            checkDataList("CustomModelWaterMark RemoveChineseOfString")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    returnType = StringClass
                }.hookAll {
                    replaceTo(waterMark)
                }
            }
        }
    }
}