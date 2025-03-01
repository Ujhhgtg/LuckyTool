package com.luckyzyx.luckytool.hook.scopes.phone

import android.content.DialogInterface
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
class ForceDisplaySIMSomeSwitch(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val volteCall = prefs(ModulePrefs).getBoolean("force_display_volte_calls", false)
        val preferredNetwork =
            prefs(ModulePrefs).getBoolean("force_display_preferred_network_type", false)

        dexKitBridge.findClass {
            matcher {
                className(
                    "com.android.simsettings.activity.OplusSimInfoActivity",
                    StringMatchType.Contains
                )
                addFieldForType(ContextClass)
                addFieldForType(StringClass)
                addFieldForType(BooleanType)
                addFieldForType(ViewClass)
                addFieldForType(DialogInterface.OnClickListener::class.java)
            }
        }.apply {
            checkDataList("ForceDisplaySIMSomeSwitch Clazz")

            //Source OplusSimInfoActivity changeVolteSwitchConfig
            findMethod {
                matcher {
                    paramTypes(IntType, null, null)
                    returnType(UnitType)
                    usingNumbers(1, 2, 3, 4, 7)
                    usingStrings("changeVolteSwitchConfig", "SIMS_OplusSimInfoActivity")
                    addUsingField {
                        type("com.coui.appcompat.preference.COUISwitchPreference")
                    }
                }
            }.apply {
                checkDataList("ForceDisplaySomeSwitch changeVolteSwitchConfig")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        param {
                            it[0] == IntType && it.contains(BooleanType) && it.contains(StringClass)
                        }
                        paramCount = 3
                    }.hook {
                        before {
                            if (!volteCall) return@before
                            val type = args().first().int()
                            val bool = args.find { it is Boolean } ?: return@before
                            val index = args.indexOf(bool).takeIf { it != -1 } ?: return@before
                            if (type == 1) args(index).setTrue()
                        }
                    }
                }
            }

            //Source OplusSimInfoActivity changeNetworkModeConfig
            findMethod {
                matcher {
                    paramTypes(IntType, null, null)
                    returnType(UnitType)
                    usingNumbers(1, 2, 5)
                    usingStrings("changeNetworkModeConfig", "SIMS_OplusSimInfoActivity")
                    addUsingField {
                        type("com.coui.appcompat.preference.COUIJumpPreference")
                    }
                }
            }.apply {
                checkDataList("ForceDisplaySomeSwitch changeNetworkModeConfig")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        param {
                            it[0] == IntType && it.contains(BooleanType) && it.contains(StringClass)
                        }
                        paramCount = 3
                    }.hook {
                        before {
                            if (!preferredNetwork) return@before
                            val type = args().first().int()
                            val bool = args.find { it is Boolean } ?: return@before
                            val index = args.indexOf(bool).takeIf { it != -1 } ?: return@before
                            if (type == 1) args(index).setTrue()
                        }
                    }
                }
            }
        }
    }
}