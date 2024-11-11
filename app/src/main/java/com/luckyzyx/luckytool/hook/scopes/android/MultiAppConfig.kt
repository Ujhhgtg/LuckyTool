package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object MultiAppConfig : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(MultiAppAllowList)

        if (prefs(ModulePrefs).getBoolean("remove_multi_app_blacklist", false)) {
            loadHooker(MultiAppBlackList)
        }
    }

    object MultiAppAllowList : YukiBaseHooker() {
        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("set_multi_app_support_mode", "0")
            dataChannel.wait<String>("set_multi_app_support_mode") { mode = it }
            var enabledMulti = prefs(ModulePrefs).getStringSet("multi_app_custom_list", ArraySet())
            dataChannel.wait<Set<String>>("multi_app_custom_list") { enabledMulti = it }
            val createdLimit =
                prefs(ModulePrefs).getBoolean("remove_multi_app_created_num_limit", false)

            //Source OplusMultiAppConfig
            "com.oplus.multiapp.OplusMultiAppConfig".toClass().apply {
                method { name = "getAllowedPkgList" }.hook {
                    before {
                        if (mode != "1" || enabledMulti.isEmpty()) return@before
                        result = java.util.ArrayList(enabledMulti)
                    }
                }
                method { name = "getMaxCreatedNum" }.hook {
                    if (createdLimit) replaceTo(1000)
                }
            }
        }
    }

    object MultiAppBlackList : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusMultiAppDataManager
            "com.android.server.pm.OplusMultiAppDataManager".toClass().apply {
                method { name = "initBlackAppList" }.hook {
                    intercept()
                }
            }
        }
    }
}