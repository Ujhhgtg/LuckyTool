package com.luckyzyx.luckytool.hook.scopes.games

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object CustomMediaPlayerSupport : YukiBaseHooker() {
    override fun onHook() {
        val set = prefs(ModulePrefs).getStringSet("custom_media_player_support_list", ArraySet())
        //Source MediaSessionHelper
        VariousClass(
            "business.module.media.MediaSessionHelper", //V8 V9
            "com.oplus.games.musicplayer.main.MediaSessionHelper" //V10
        ).toClass().apply {
            method {
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