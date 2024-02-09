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
//
//        //Source GameEventBean
//        "com.oplus.vrr.bean.GameEventBean".toClass().apply {
//            method { name = "setBackLightBean" }.hook {
//                before {
//                    val bean = args().first().any() ?: return@before
//                    YLog.info("setBackLightBean -> ${bean.toString()}")
//
//                    bean.current().method { name = "setEnable" }.call(false)
//                    val mNitsToMinFPS = bean.current().field { name = "mNitsToMinFPS" }
//                        .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()
//                    YLog.info("setBackLightBean mNitsToMinFPS -> ${!mNitsToMinFPS.isNullOrEmpty()}")
//
//                    mNitsToMinFPS?.clear()
//                }
//            }
//            method { name = "setPwmBackLightBean" }.hook {
//                before {
//                    val bean = args().first().any() ?: return@before
//                    YLog.info("setPwmBackLightBean -> ${bean.toString()}")
//
//                    bean.current().method { name = "setEnable" }.call(false)
//                    val mNitsToMinFPS = bean.current().field { name = "mNitsToMinFPS" }
//                        .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()
//                    YLog.info("setPwmBackLightBean mNitsToMinFPS -> ${!mNitsToMinFPS.isNullOrEmpty()}")
//
//                    mNitsToMinFPS?.clear()
//                }
//            }
//            method { name = "setSinglePulseBackLightBean" }.hook {
//                before {
//                    val bean = args().first().any() ?: return@before
//                    YLog.info("setSinglePulseBackLightBean -> ${bean.toString()}")
//
//                    bean.current().method { name = "setEnable" }.call(false)
//                    val mNitsToMinFPS = bean.current().field { name = "mNitsToMinFPS" }
//                        .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()
//                    YLog.info("setSinglePulseBackLightBean mNitsToMinFPS -> ${!mNitsToMinFPS.isNullOrEmpty()}")
//
//                    mNitsToMinFPS?.clear()
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