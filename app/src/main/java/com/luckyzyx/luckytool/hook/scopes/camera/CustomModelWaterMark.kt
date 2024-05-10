package com.luckyzyx.luckytool.hook.scopes.camera

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class CustomModelWaterMark(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val waterMark = prefs(ModulePrefs).getString("custom_model_watermark", "None")
        if (waterMark.isBlank() || waterMark == "None") return

        //Source BaseWatermarkPresenter / BaseWatermarkCreator
        dexKitBridge.findMethod {
            matcher {
                paramTypes(ContextClass, FloatType, null, null)
                usingStrings(
                    "key_watermark_part_a_line",
                    "key_watermark_part_b_line"
                )
                usingNumbers(0.03F,0.007F)
            }
        }.apply {
            checkDataList("CustomModelWaterMark Shot")
            single().className.toClass().apply {
                method {
                    name(single().methodName)
                    param(ContextClass, FloatType, VagueType, VagueType)
                    returnType(single().returnTypeName)
                }.hook {
                    before {
                        val model = args().last().string()
                        if (model == "Shot on OnePlus") args().last().set(waterMark)
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
            var clazz = ""
            forEach {
                if (clazz.isBlank() || clazz != it.className) clazz = it.className
                clazz.toClass().apply {
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
                paramCount(1..2)
                returnType(StringClass)
                usingStrings("[\u4e00-\u9fa5]", "")
            }
        }.apply {
            checkDataList("CustomModelWaterMark ChineseOfString")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    paramCount(1..2)
                    returnType = StringClass
                }.hookAll {
                    replaceTo(waterMark)
                }
            }
        }
    }
}