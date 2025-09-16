package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object MultiAppConfig : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        loadHooker(MultiAppAllowList)

        if (prefs(ModulePrefs).getBoolean("remove_multi_app_blacklist", false)) {
            if (osCode >= 31) loadHooker(MultiAppBlackList)
        }
    }

    @Obfuscate
    object MultiAppAllowList : YukiBaseHooker() {

        var mode = "0"
        val list = ArrayList<String>()
        var limit = false

        private fun loadData() {
            mode = prefs(ModulePrefs).getString("set_multi_app_support_mode", "0")
            dataChannel.wait<String>("set_multi_app_support_mode") {
                mode = it
                YLog.debug("update multi app configs status -> $it")
            }

            list.clear()
            list.addAll(prefs(ModulePrefs).getStringSet("multi_app_custom_list", ArraySet()))
            dataChannel.wait("multi_app_custom_list") {
                list.clear()
                val new = prefs(ModulePrefs).getStringSet("multi_app_custom_list", ArraySet())
                list.addAll(new)
                YLog.debug("update multi app whitelist configs -> ${list.size} | ${new.size}")
            }
            limit = prefs(ModulePrefs).getBoolean("remove_multi_app_created_num_limit", false)
            YLog.debug("init multi app configs success")
        }

        override fun onHook() {
            loadData()

            //Source OplusMultiAppConfig
            "com.oplus.multiapp.OplusMultiAppConfig".toClass().resolve().apply {
                firstMethod { name = "getAllowedPkgList" }.hook {
                    before {
                        if (mode != "1" || list.isEmpty()) return@before
                        result = java.util.ArrayList(list)
                    }
                }
                firstMethod { name = "getMaxCreatedNum" }.hook {
                    if (limit) replaceTo(1000)
                }
            }
        }
    }

    @Obfuscate
    object MultiAppBlackList : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusMultiAppDataManager
            "com.android.server.pm.OplusMultiAppDataManager".toClass().resolve().apply {
                firstMethod { name = "initBlackAppList" }.hook {
                    intercept()
                }
            }
        }
    }
}