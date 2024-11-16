package com.luckyzyx.luckytool.hook.scopes.games

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object CustomBarrageNotificationWhitelist : YukiBaseHooker() {
    override fun onHook() {
        val set = prefs(ModulePrefs).getStringSet(
            "custom_barrage_notification_whitelist_list", ArraySet()
        )

        //Source GameBarrageUtil
        "com.coloros.gamespaceui.module.barrage.GameBarrageUtil".toClass().apply {
            if (set.isEmpty()) return
            field { name = "supportPackagesDefault" }.get().apply {
                set(
                    array<String>().toMutableList().apply {
                        addAll(set)
                    }.toTypedArray()
                )
            }
        }
    }
}