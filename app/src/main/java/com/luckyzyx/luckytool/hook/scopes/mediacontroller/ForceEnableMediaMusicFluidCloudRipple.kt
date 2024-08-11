package com.luckyzyx.luckytool.hook.scopes.mediacontroller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.json.JSONObject

object ForceEnableMediaMusicFluidCloudRipple : YukiBaseHooker() {
    override fun onHook() {
        //Source SeedlingTool
        "com.oplus.pantanal.seedling.util.SeedlingTool".toClass().apply {
            method { name { it.startsWith("update") && it.endsWith("Data") } }.hookAll {
                before {
                    val json = args(1).cast<JSONObject>() ?: return@before
                    val staticVoicePrintShow = json.optBoolean("staticVoicePrintShow", true)
                    if (staticVoicePrintShow) {
                        json.put("staticVoicePrintShow", false)
                    }
                }
            }
        }
    }
}