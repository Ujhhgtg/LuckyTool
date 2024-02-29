package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object LTPODynamicRefreshRate : YukiBaseHooker() {

    private const val BackLightBean = "com.oplus.vrr.bean.BackLightBean"

    override fun onHook() {
        val ltpoMode = prefs(ModulePrefs).getString("set_ltpo_refresh_rate_mode", "0")
        val ltpoMinOne =
            prefs(ModulePrefs).getBoolean("enable_full_brightness_refresh_rate_minimum_one", false)

        if (ltpoMode != "1") return

        //Source BackLightBean
        "com.oplus.vrr.OPlusFeatureManager".toClass().apply {
            method {
                name { it.startsWith("on") }
                param(BackLightBean)
            }.hookAll {
                before {
                    if (!ltpoMinOne) return@before
                    val bean = args().first().any() ?: return@before
                    val mNitsToMinFPS = bean.current().field {
                        name = "mNitsToMinFPS"
                    }.cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()
                    mNitsToMinFPS?.onEach { (fps, list) ->
                        list.forEach { map ->
                            map.keys.forEach {
                                map[it] = 1F
                            }
                        }
                        mNitsToMinFPS[fps] = list
                    }
                }
            }
        }

        //Source OPlusOnlineConfigManager
        "com.oplus.vrr.OPlusOnlineConfigManager".toClass().apply {
            method { name = "createGameEvent" }.hook {
                after {
                    val bean = result<Any>() ?: return@after

//                    val mPkgNames = bean.current().method { name = "getPkgNames" }.call()
//                    YLog.info("GameEventBean ${mPkgNames.toString()}")

                    val mBackLightBean = bean.current().field { name = "mBackLightBean" }.any()
                    if (mBackLightBean != null) {
                        mBackLightBean.current().field { name = "mEnable" }.setFalse()
                        mBackLightBean.current().field { name = "mNitsToMinFPS" }
                            .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
                    }

                    val mPwmBackLightBean =
                        bean.current().field { name = "mPwmBackLightBean" }.any()
                    if (mPwmBackLightBean != null) {
                        mPwmBackLightBean.current().field { name = "mEnable" }.setFalse()
                        mPwmBackLightBean.current().field { name = "mNitsToMinFPS" }
                            .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
                    }

                    val mSinglePulseBackLightBean =
                        bean.current().field { name = "mBackLightBean" }.any()
                    if (mSinglePulseBackLightBean != null) {
                        mSinglePulseBackLightBean.current().field { name = "mEnable" }.setFalse()
                        mSinglePulseBackLightBean.current().field { name = "mNitsToMinFPS" }
                            .cast<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
                    }
                }
            }
        }

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