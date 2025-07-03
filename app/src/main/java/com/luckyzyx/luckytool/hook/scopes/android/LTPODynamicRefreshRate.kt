package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object LTPODynamicRefreshRate : YukiBaseHooker() {

    private const val BackLightBean = "com.oplus.vrr.bean.BackLightBean"

    override fun onHook() {
        val ltpoMode = prefs(ModulePrefs).getString("set_ltpo_refresh_rate_mode", "0")
        val ltpoMinOne =
            prefs(ModulePrefs).getBoolean("enable_full_brightness_refresh_rate_minimum_one", false)

        if (ltpoMode != "1") return

        //Source BackLightBean
        "com.oplus.vrr.OPlusFeatureManager".toClass().resolve().optional().apply {
            method {
                name { it.startsWith("on") }
                parameters(BackLightBean)
            }.hookAll {
                before {
                    if (!ltpoMinOne) return@before
                    val bean = args().first().any() ?: return@before
                    val mNitsToMinFPS = bean.resolve().firstField {
                        name = "mNitsToMinFPS"
                    }.get<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()
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
        "com.oplus.vrr.OPlusOnlineConfigManager".toClass().resolve().optional().apply {
            firstMethod { name = "createGameEvent" }.hook {
                after {
                    val bean = result<Any>() ?: return@after

//                    val mPkgNames = bean.current().method { name = "getPkgNames" }.call()
//                    YLog.info("GameEventBean ${mPkgNames.toString()}")

                    val mBackLightBean = bean.resolve().firstField { name = "mBackLightBean" }.get()
                    if (mBackLightBean != null) {
                        mBackLightBean.resolve().firstField { name = "mEnable" }.set(false)
                        mBackLightBean.resolve().firstField { name = "mNitsToMinFPS" }
                            .get<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
                    }

                    val mPwmBackLightBean =
                        bean.resolve().firstField { name = "mPwmBackLightBean" }.get()
                    if (mPwmBackLightBean != null) {
                        mPwmBackLightBean.resolve().firstField { name = "mEnable" }.set(false)
                        mPwmBackLightBean.resolve().firstField { name = "mNitsToMinFPS" }
                            .get<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
                    }

                    val mSinglePulseBackLightBean =
                        bean.resolve().firstField { name = "mBackLightBean" }.get()
                    if (mSinglePulseBackLightBean != null) {
                        mSinglePulseBackLightBean.resolve().firstField { name = "mEnable" }
                            .set(false)
                        mSinglePulseBackLightBean.resolve().firstField { name = "mNitsToMinFPS" }
                            .get<HashMap<Int, ArrayList<HashMap<Float, Float>>>>()?.clear()
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