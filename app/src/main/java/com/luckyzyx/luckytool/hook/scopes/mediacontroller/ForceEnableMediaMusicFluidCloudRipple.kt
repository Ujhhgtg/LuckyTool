package com.luckyzyx.luckytool.hook.scopes.mediacontroller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.json.JSONObject

object ForceEnableMediaMusicFluidCloudRipple : YukiBaseHooker() {
    override fun onHook() {
        var isEnable =
            prefs(ModulePrefs).getBoolean("force_enable_media_music_fluid_cloud_ripple", false)
        dataChannel.wait<Boolean>("force_enable_media_music_fluid_cloud_ripple") { isEnable = it }

        //Source SeedlingTool
        "com.oplus.pantanal.seedling.util.SeedlingTool".toClass().apply {
            method { name { it.startsWith("update") && it.endsWith("Data") } }.hookAll {
                before {
                    if (!isEnable) return@before
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