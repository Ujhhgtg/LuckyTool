package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method

object HookAIUnit : YukiBaseHooker() {
    override fun onHook() {
        //Source Router
        "com.oplus.aiunit.router.Router".toClass().apply {
            method { name = "getDefaultConfiguration" }.hook {
                after {
                    val list = result<List<Any>>()?.takeIf { it.isNotEmpty() } ?: return@after
                    list.forEachIndexed { _, it ->
//                            YLog.info("$index -> ${it.toString()}")
                        val unitName = it.current().method { name = "getUnitName" }.string()
                        when (unitName) {
                            "cloud_aigc_segmentation" -> {
                                it.current().method { name = "setDisabled" }.call(false)
                                it.current().method { name = "setWhiteModels" }.call("")
                            }

                            "cloud_aigc_sdinpainting" -> {
                                it.current().method { name = "setDisabled" }.call(false)
                                it.current().method { name = "setWhiteModels" }.call("")
                            }
                        }
                    }
                }
            }
        }
    }
}