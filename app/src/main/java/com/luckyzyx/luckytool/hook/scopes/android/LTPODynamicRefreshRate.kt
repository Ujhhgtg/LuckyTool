package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object LTPODynamicRefreshRate : YukiBaseHooker() {

    override fun onHook() {
        var ltpoMinOne =
            prefs(ModulePrefs).getBoolean("enable_full_brightness_refresh_rate_minimum_one", false)
        dataChannel.wait<Boolean>("enable_full_brightness_refresh_rate_minimum_one") {
            ltpoMinOne = it
        }

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
//
//        //Source GameEventBean
//        "com.oplus.vrr.bean.GameEventBean".toClass().apply {
//            method { name = "getBackLightBean" }.hook {
//                after {
//                    val bean = result<Any>() ?: return@after
//                    bean.current().method { name = "setEnable" }.call(false)
//                    YLog.info("getBackLightBean -> ${bean.toString()}")
//                }
//            }
//            method { name = "getPwmBackLightBean" }.hook {
//                after {
//                    val bean = result<Any>() ?: return@after
//                    bean.current().method { name = "setEnable" }.call(false)
//                    YLog.info("getPwmBackLightBean -> ${bean.toString()}")
//                }
//            }
//            method { name = "getSinglePulseBackLightBean" }.hook {
//                after {
//                    val bean = result<Any>() ?: return@after
//                    bean.current().method { name = "setEnable" }.call(false)
//                    YLog.info("getSinglePulseBackLightBean -> ${bean.toString()}")
//                }
//            }
//        }
//
//        //Source AVTBean
//        "com.oplus.vrr.bean.AVTBean".toClass().apply {
//            method { name = "isEnable" }.hook {
//                replaceToFalse()
//            }
//            method { name = "getAvtMinFps" }.hook {
//                replaceTo(1)
//            }
//        }
//
//        //Source TouchIdleBean
//        "com.oplus.vrr.bean.TouchIdleBean".toClass().apply {
//            method { name = "isEnable" }.hook {
//                replaceToTrue()
//            }
//            method { name = "isHwEnable" }.hook {
//                replaceToTrue()
//            }
//            method { name = "isSwEnable" }.hook {
//                replaceToTrue()
//            }
//            method { name = "isAdfrEnable" }.hook {
//                replaceToTrue()
//            }
//        }
//
//        //Source OPlusExternalRefreshRateManager normalized_minfps
//        "com.oplus.vrr.OPlusExternalRefreshRateManager".toClass().apply {
//            method { name = "setAdfrMinFpsConfig" }.hook {
//                before {
//                    args().first().setTrue()
//                }
//            }
//        }

    }
}