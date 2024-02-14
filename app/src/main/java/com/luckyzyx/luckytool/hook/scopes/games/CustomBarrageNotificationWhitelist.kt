package com.luckyzyx.luckytool.hook.scopes.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.replaceSpace

object CustomBarrageNotificationWhitelist : YukiBaseHooker() {
    override fun onHook() {
        val customList =
            prefs(ModulePrefs).getString("custom_barrage_notification_whitelist", "None")

        //Source GameBarrageUtil
        "com.coloros.gamespaceui.module.barrage.GameBarrageUtil".toClass().apply {
            if (customList.isBlank() || customList == "None") return
            field { name = "supportPackagesDefault" }.get().apply {
                set(
                    array<String>().toMutableList().apply {
                        val listString = customList.replaceSpace
                        if (listString.contains("\n")) {
                            listString.split("\n").forEach { if (it.isNotBlank()) add(it) }
                        } else add(customList)
                    }.toTypedArray()
                )
            }
        }
    }
}