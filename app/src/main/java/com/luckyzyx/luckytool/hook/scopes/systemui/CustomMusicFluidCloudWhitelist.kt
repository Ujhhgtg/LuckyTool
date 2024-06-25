package com.luckyzyx.luckytool.hook.scopes.systemui

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object CustomMusicFluidCloudWhitelist : YukiBaseHooker() {
    override fun onHook() {
        var set =
            prefs(ModulePrefs).getStringSet("set_custom_music_fluid_cloud_whitelist", ArraySet())
        dataChannel.wait<Set<String>>("set_custom_music_fluid_cloud_whitelist") { set = it }

        //Source OplusMediaRusUpdateManager
        "com.oplus.systemui.media.seedling.rus.OplusMediaRusUpdateManager".toClass().apply {
            method { name = "getLocalDataToSP";returnType = ListClass }.hook {
                after {
                    if (set.isEmpty()) return@after
                    val list = result<ArrayList<String>>() ?: return@after
                    result = java.util.ArrayList(LinkedHashSet<String>().apply {
                        addAll(list)
                        addAll(set)
                    })
                }
            }
        }
    }
}