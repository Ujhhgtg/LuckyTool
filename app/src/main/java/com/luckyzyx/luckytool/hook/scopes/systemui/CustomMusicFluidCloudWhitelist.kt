package com.luckyzyx.luckytool.hook.scopes.systemui

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object CustomMusicFluidCloudWhitelist : YukiBaseHooker() {
    override fun onHook() {
        val disabled = prefs(ModulePrefs).getBoolean("disable_music_fluid_cloud_display", false)
        val set =
            prefs(ModulePrefs).getStringSet("set_custom_music_fluid_cloud_whitelist", ArraySet())

        //Source OplusMediaRusUpdateManager
        "com.oplus.systemui.media.seedling.rus.OplusMediaRusUpdateManager".toClass().apply {
            method { name = "getRusWhiteList";returnType = ListClass }.hook {
                after {
                    val originalList = result<java.util.ArrayList<String>>() ?: return@after
                    if (disabled) {
                        originalList.clear()
                    } else if (set.isNotEmpty()) {
                        val finalList = LinkedHashSet<String>().apply {
                            addAll(originalList)
                            addAll(set)
                        }
                        originalList.clear()
                        originalList.addAll(finalList)
                    }
                }
            }
        }
    }
}