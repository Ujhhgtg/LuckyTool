package com.luckyzyx.luckytool.hook.scope.permissioncontroller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.SDK
import org.luckypray.dexkit.query.enums.UsingType

object UnlockDefaultDesktopLimit : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A13) loadHooker(UnlockDefaultDesktop)
        else loadHooker(UnlockDefaultDesktopV12)
    }

    object UnlockDefaultDesktop : YukiBaseHooker() {
        override fun onHook() {
            //Source FeatureOption -> oplus.software.defaultapp.remove_force_launcher
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findMethod {
                    matcher {
                        addUsingField {
                            field {
                                addPutMethod {
                                    paramTypes(ContextClass.name)
                                    returnType(UnitType)
                                    usingStrings(
                                        "oplus.software.pms_app_frozen",
                                        "oplus.software.defaultapp.remove_force_launcher",
                                        "oplus.hardware.type.tablet"
                                    )
                                }
                                addGetMethod {
                                    paramCount(0)
                                    returnType(BooleanType)
                                }
                            }
                            usingType(UsingType.Get)
                        }
                        paramCount(0)
                        returnType(BooleanType.name)
                        callMethods {
                            add {
                                paramTypes("java.util.List")
                                returnType(UnitType)
                            }
                            count(1)
                        }
                    }
                }.apply {
                    checkDataList("UnlockDefaultDesktopLimit finalMethod")
                    val member = first()
                    member.className.toClass().apply {
                        method { name = member.methodName }.hook {
                            replaceToTrue()
                        }
                    }
                }
            }
        }
    }

    object UnlockDefaultDesktopV12 : YukiBaseHooker() {
        override fun onHook() {
            //Source FeatureOption -> oplus.software.defaultapp.remove_force_launcher
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findMethod {
                    matcher {
                        addUsingField {
                            field {
                                addPutMethod {
                                    paramTypes(ContextClass.name)
                                    returnType(UnitType)
                                    usingStrings(
                                        "oplus.software.pms_app_frozen",
                                        "oplus.software.defaultapp.remove_force_launcher",
                                        "oplus.hardware.type.tablet"
                                    )
                                }
                                addGetMethod {
                                    paramCount(0)
                                    returnType(BooleanType)
                                }
                            }
                            usingType(UsingType.Get)
                        }
                        paramCount(0)
                        returnType(BooleanType.name)
                        addCall {
                            paramTypes("java.util.List")
                            returnType(UnitType)
                        }
                    }
                }.apply {
                    checkDataList("UnlockDefaultDesktopLimitV12")
                    val member = first()
                    member.className.toClass().apply {
                        method { name = member.methodName;emptyParam() }.hook {
                            replaceToTrue()
                        }
                    }
                }
            }
        }
    }
}