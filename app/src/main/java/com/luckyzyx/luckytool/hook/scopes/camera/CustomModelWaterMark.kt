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

        //Source BaseWatermarkPresenter
        dexKitBridge.findMethod {
            matcher {
                paramTypes(ContextClass, FloatType, null, StringClass)
                usingStrings(
                    "BaseSloganUtil",
                    "com.oplus.device_series",
                    "key_watermark_part_a_line",
                    "key_watermark_part_b_line"
                )
            }
        }.apply {
            checkDataList("CustomModelWaterMark Shot")
            single().className.toClass().apply {
                method {
                    name(single().methodName)
                    param(ContextClass, FloatType, VagueType, StringClass)
                    returnType(single().returnTypeName)
                }.hook {
                    before {
                        val model = args().last().string()
                        if (model == "Shot on OnePlus") args().last().set(waterMark)
                    }
                }
            }
        }

        //Source MarketUtil
        dexKitBridge.findMethod {
            matcher {
                paramCount(0)
                returnType(StringClass)
                usingStrings(
                    "", "ro.vendor.oplus.market.enname", "ro.vendor.oplus.market.name"
                )
            }
        }.apply {
            checkDataList("CustomModelWaterMark MarketUtil", false, isDebug = true)
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

        //Source WatermarkHelper
        dexKitBridge.findMethod {
            matcher {
                paramTypes(StringClass)
                returnType(StringClass)
                usingStrings("WatermarkHelper", "[\u4e00-\u9fa5]", "")
            }
        }.apply {
            checkDataList("CustomModelWaterMark WatermarkHelper", isDebug = true)
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    param(StringClass)
                    returnType = StringClass
                }.hookAll {
                    replaceTo(waterMark)
                }
            }
        }
    }
}