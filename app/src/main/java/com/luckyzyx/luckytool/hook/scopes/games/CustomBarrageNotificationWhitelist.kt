package com.luckyzyx.luckytool.hook.scopes.games

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object CustomBarrageNotificationWhitelist : YukiBaseHooker() {
    override fun onHook() {
        val set = prefs(ModulePrefs).getStringSet(
            "custom_barrage_notification_whitelist_list", ArraySet()
        )

        //Source GameBarrageUtil
        "com.coloros.gamespaceui.module.barrage.GameBarrageUtil".toClass().apply {
            method { name = "initAppState" }.hook {
                before {
                    if (set.isEmpty()) return@before

                    val gameBarrageApplicationState = method {
                        name = "getGameBarrageApplicationState"
                    }.get().invoke<HashMap<String, String>>() ?: return@before
                    if (gameBarrageApplicationState.isEmpty() || gameBarrageApplicationState.size != set.size) {
                        set.forEachIndexed { _, s ->
                            if (!gameBarrageApplicationState.containsKey(s)) {
                                gameBarrageApplicationState[s] = "1"
                            }
                        }
                        method { name = "setGameBarrageApplicationState" }.get()
                            .call(gameBarrageApplicationState)
                    }
                    result = gameBarrageApplicationState
                }
            }
            method { name = "getGameBarrageAppSwitchMap" }.hook {
                before {
                    if (set.isEmpty()) return@before

                    val hashMap = java.util.HashMap<String, String>()
                    val gameBarrageApplicationState = method {
                        name = "getGameBarrageApplicationState"
                    }.get().invoke<HashMap<String, String>>() ?: return@before
                    set.forEachIndexed { _, s ->
                        hashMap[s] = gameBarrageApplicationState.getOrDefault(s, "1")
                    }
                    result = hashMap
                }
            }
        }
    }
}