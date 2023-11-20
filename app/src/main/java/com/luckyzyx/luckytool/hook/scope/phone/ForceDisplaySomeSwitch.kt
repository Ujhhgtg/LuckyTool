package com.luckyzyx.luckytool.hook.scope.phone

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class ForceDisplaySomeSwitch(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val volteCall = prefs(ModulePrefs).getBoolean("force_display_volte_calls", false)
        val preferredNetwork =
            prefs(ModulePrefs).getBoolean("force_display_preferred_network_type", false)

        //Source OplusSimInfoActivity changeVolteSwitchConfig
        dexKitBridge.findMethod {
            searchPackages("com.android.simsettings.activity.OplusSimInfoActivity")
            matcher {
                paramTypes(IntType, BooleanType, StringClass)
                returnType(UnitType)
                usingNumbers(1, 2, 3, 4, 7)
                usingStrings("changeVolteSwitchConfig")
                declaredClass {
                    usingStrings("SIMS_OplusSimInfoActivity")
                }
            }
        }.apply {
            checkDataList("ForceDisplaySomeSwitch changeVolteSwitchConfig")
            fetchOne().className.toClass().apply {
                method {
                    name = fetchOne().methodName
                    param(IntType, BooleanType, StringClass)
                }.hook {
                    before {
                        if (!volteCall) return@before
                        val type = args().first().int()
                        if (type == 1) args(1).setTrue()
                    }
                }
            }
        }
        //Source OplusSimInfoActivity changeNetworkModeConfig
        dexKitBridge.findMethod {
            searchPackages("com.android.simsettings.activity.OplusSimInfoActivity")
            matcher {
                paramTypes(IntType, BooleanType, StringClass)
                returnType(UnitType)
                usingNumbers(1, 2, 5)
                usingStrings("changeNetworkModeConfig")
                declaredClass {
                    usingStrings("SIMS_OplusSimInfoActivity")
                }
            }
        }.apply {
            checkDataList("ForceDisplaySomeSwitch changeNetworkModeConfig")
            fetchOne().className.toClass().apply {
                method {
                    name = fetchOne().methodName
                    param(IntType, BooleanType, StringClass)
                }.hook {
                    before {
                        if (!preferredNetwork) return@before
                        val type = args().first().int()
                        if (type == 1) args(1).setTrue()
                    }
                }
            }
        }
    }
}