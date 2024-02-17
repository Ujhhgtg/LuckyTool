package com.luckyzyx.luckytool.hook.scopes.games

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object CustomMediaPlayerSupport : YukiBaseHooker() {
    override fun onHook() {
        val set = prefs(ModulePrefs).getStringSet("custom_media_player_support_list", ArraySet())
        //Source MediaSessionHelper
        "business.module.media.MediaSessionHelper".toClass().apply {
            method {
                modifiers { isStatic }
                emptyParam()
                returnType = ListClass
            }.hook {
                after {
                    if (set.isEmpty()) return@after
                    val list = result<List<String>>() ?: return@after
                    result = list.toMutableList().apply {
                        addAll(set)
                    }
                }
            }
        }
    }
}