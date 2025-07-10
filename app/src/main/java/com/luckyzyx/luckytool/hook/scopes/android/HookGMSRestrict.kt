package com.luckyzyx.luckytool.hook.scopes.android

import android.util.SparseArray
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookGMSRestrict : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        val isEnable = prefs(ModulePrefs).getBoolean("remove_gms_usage_restrictions", false)
        if (!isEnable) return

        //Source OplusAppStartupManager -> OplusStartupStrategy -> google_restric_info
        "com.android.server.am.OplusAppStartupManager\$OplusStartupStrategy".toClass().resolve()
            .apply {
                firstMethod { name = "isGoogleRestricInfoOn" }.hook {
                    replaceToFalse()
                }
            }

        //Source OplusHansDBConfig -> sys_elsa_config_list -> Athena
        "com.android.server.hans.OplusHansDBConfig".toClass().resolve().apply {
            method { name = "updateManagedMap" }.hookAll {
                after {
                    firstField { name = "mGMSList" }.of(instance).get<SparseArray<Any>>()?.clear()
                }
            }
            firstMethod { name = "updateTargetList" }.hook {
                after {
                    firstField { name = "mGMSList" }.of(instance).get<SparseArray<Any>>()?.clear()
                }
            }
        }

        if (osCode > 30) loadHooker(GMSRestrict)
        else loadHooker(GMSRestrictV13)
    }

    @Obfuscate
    object GMSRestrict : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusBgSceneManager -> google_restric_info
            "com.android.server.hans.scene.OplusBgSceneManager".toClass().resolve().apply {
                firstMethod { name = "setGmsRestricted" }.hook {
                    before {
                        args().first().setFalse()
                    }
                }
                firstMethod { name = "isGmsRestricted" }.hook {
                    replaceToFalse()
                }
                firstMethod { name = "registerGmsRestrictObserver" }.hook {
                    intercept()
                }
            }
        }
    }

    @Obfuscate
    object GMSRestrictV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusHansManager -> HansConfig -> google_restric_info
            "com.android.server.am.OplusHansManager\$HansConfig".toClass().resolve().apply {
                firstMethod { name = "setGmsRestricted" }.hook {
                    before {
                        args().first().setFalse()
                    }
                }
                firstMethod { name = "isGmsRestricted" }.hook {
                    replaceToFalse()
                }
            }
            //Source OplusHansManager -> HansTrigger -> google_restric_info
            "com.android.server.am.OplusHansManager\$HansTrigger".toClass().resolve().apply {
                firstMethod { name = "registerGmsRestrictObserver" }.hook {
                    intercept()
                }
            }
        }
    }
}