package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ReplaceOnePlusModelWatermark : YukiBaseHooker() {
    override fun onHook() {
        //Source WatermarkContent
        "com.oplus.tbluniformeditor.plugins.watermark.data.WatermarkContent".toClass().apply {
            constructor { }.hookAll {
                after {
                    method { name = "setMake" }.get(instance).call("")
                }
            }
        }
    }
}