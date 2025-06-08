package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class ReplaceOnePlusModelWatermark(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val waterMark = prefs(ModulePrefs).getString("custom_model_watermark", "None")

        //Source WatermarkContent
        "com.oplus.tbluniformeditor.plugins.watermark.data.WatermarkContent".toClass().apply {
            method { name = "getMake" }.hook {
                replaceTo("")
            }
        }

        //Source MarketNameInfo -> com.oplus.camera -> ro.vendor.oplus.market.name / ro.vendor.oplus.market.enname
        dexKitBridge.findClass {
            matcher {
                addFieldForType(StringClass)
                addMethod { paramCount(0);returnType(StringClass) }
                usingStrings(
                    "MarketNameInfo",
                    "ro.vendor.oplus.market.name",
                    "ro.vendor.oplus.market.enname"
                )
            }
        }.apply {
            checkDataList("ReplaceOnePlusModelWatermark MarketNameInfo")
            single().name.toClass().apply {
                method { emptyParam();returnType = StringClass }.hookAll {
                    before {
                        if (waterMark.isBlank() || waterMark == "None") return@before
                        result = waterMark
                    }
                }
            }
        }
    }
}