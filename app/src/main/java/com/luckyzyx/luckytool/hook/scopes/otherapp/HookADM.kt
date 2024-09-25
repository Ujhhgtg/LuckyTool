package com.luckyzyx.luckytool.hook.scopes.otherapp

import android.app.Activity
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.query.enums.StringMatchType

object HookADM : YukiBaseHooker() {
    override fun onHook() {
        if (prefs(ModulePrefs).getBoolean("adm_unlock_pro", false)) {
            loadHooker(UnlockAdmPro)
        }
        if (prefs(ModulePrefs).getBoolean("adm_unlock_threads", false)) {
            loadHooker(UnlockAdmThreads)
        }
    }

    object UnlockAdmPro : YukiBaseHooker() {
        override fun onHook() {
            //Source Main
            "com.dv.get.Main".toClass().apply {
                method { name = "onCreate" }.hook {
                    after {
                        val activity = instance<Activity>()
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        sp.edit().putBoolean("hua_voices", false).commit()
                    }
                }
            }
        }
    }

    object UnlockAdmThreads : YukiBaseHooker() {
        override fun onHook() {
            //Source Main -> S215
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findClass {
                    matcher {
                        addFieldForType(IntType)
                        methods {
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(StringClass)
                            }
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(IntType)
                            }
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(UnitType)
                            }
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(BooleanType)
                            }
                        }
                        usingStrings("DOWN_ALGORITM_3GWF", "DOWN_RESTART")
                    }
                }.apply {
                    checkDataList("UnlockAdmThreads")
                    single().name.toClass().apply {
                        method { emptyParam();returnType = IntType }.hook {
                            before {
                                val type = field { type = IntType }.get(instance).int()
                                when (type) {
                                    15 -> result = 64 - 1
                                    16 -> result = 64 - 1
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}