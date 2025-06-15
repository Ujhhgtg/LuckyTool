package com.luckyzyx.luckytool.hook.scopes.android

import android.util.SparseArray
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookGMSRestrict : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        val isEnable = prefs(ModulePrefs).getBoolean("remove_gms_usage_restrictions", false)
        if (!isEnable) return

        if (osCode >= 30) loadHooker(GMSRestrict)
        else loadHooker(GMSRestrictV13)

        //Source OplusAppStartupManager -> OplusStartupStrategy -> google_restric_info
        "com.android.server.am.OplusAppStartupManager\$OplusStartupStrategy".toClass().apply {
            method { name = "isGoogleRestricInfoOn" }.hook {
                replaceToFalse()
            }
        }

        //Source OplusHansDBConfig -> sys_elsa_config_list -> Athena
        "com.android.server.hans.OplusHansDBConfig".toClass().apply {
            method { name = "updateManagedMap";paramCount = 3 }.hook {
                after {
                    field { name = "mGMSList" }.get(instance).cast<SparseArray<Any>>()?.clear()
                }
            }
            method { name = "updateTargetList" }.hook {
                after {
                    field { name = "mGMSList" }.get(instance).cast<SparseArray<Any>>()?.clear()
                }
            }
        }
    }

    object GMSRestrict : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusBgSceneManager -> google_restric_info
            "com.android.server.hans.scene.OplusBgSceneManager".toClass().apply {
                method { name = "setGmsRestricted" }.hook {
                    before {
                        args().first().setFalse()
                    }
                }
                method { name = "isGmsRestricted" }.hook {
                    replaceToFalse()
                }
                method { name = "registerGmsRestrictObserver" }.hook {
                    intercept()
                }
            }
        }
    }

    object GMSRestrictV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusHansManager -> HansConfig -> google_restric_info
            "com.android.server.am.OplusHansManager\$HansConfig".toClass().apply {
                method { name = "setGmsRestricted" }.hook {
                    before {
                        args().first().setFalse()
                    }
                }
                method { name = "isGmsRestricted" }.hook {
                    replaceToFalse()
                }
            }
            //Source OplusHansManager -> HansTrigger -> google_restric_info
            "com.android.server.am.OplusHansManager\$HansTrigger".toClass().apply {
                method { name = "registerGmsRestrictObserver" }.hook {
                    intercept()
                }
            }
        }
    }
}