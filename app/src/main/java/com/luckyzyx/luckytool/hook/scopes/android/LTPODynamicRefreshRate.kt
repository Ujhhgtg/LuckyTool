package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object LTPODynamicRefreshRate : YukiBaseHooker() {

    override fun onHook() {
        val ltpoMinOne =
            prefs(ModulePrefs).getBoolean("enable_full_brightness_refresh_rate_minimum_one", false)

        //Source BackLightBean
        "com.oplus.vrr.bean.BackLightBean".toClass().apply {
            method { name = "getStrategyList" }.hook {
                after {
                    if (!ltpoMinOne) return@after
                    result<ArrayList<HashMap<Float, Float>>>()?.forEachIndexed { _, map ->
                        map.keys.forEachIndexed { _, nits ->
                            map[nits] = 1F
                        }
                    }
                }
            }
        }
    }
}