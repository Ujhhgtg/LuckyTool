package com.luckyzyx.luckytool.hook.scopes.securepay

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.CheckBoxClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DialogInterfaceClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList

object RemoveSecurePayFoundVirusDialog : YukiBaseHooker() {
    override fun onHook() {
        //Source RiskDialogWrapper
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            dexKitBridge.findClass {
                searchPackages("com.coloros.securepay")
                matcher {
                    fields {
                        addForType(BooleanType)
                        addForType(CheckBoxClass)
                    }
                    methods {
                        add { paramCount(0);returnType(UnitType) }
                        add { paramCount(4..8);returnType(UnitType) }
                        add { paramCount(0);returnType(BooleanType) }
                        add { paramTypes(ViewClass);returnType(UnitType) }
                        add {
                            paramTypes(
                                ContextClass, StringClass, IntType, DialogInterfaceClass, IntType
                            )
                            returnType(UnitType)
                        }
                    }
                }
            }.apply {
                checkDataList("RemoveSecurePayFoundVirusDialog")
                single().name.toClass().apply {
                    method { param(VagueType, StringClass);returnType = UnitType }.hook {
                        intercept()
                    }
                    method { emptyParam();returnType = UnitType }.hookAll {
                        intercept()
                    }
                }
            }
        }
    }
}