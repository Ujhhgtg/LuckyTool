package com.luckyzyx.luckytool.hook.scope.settings

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList

object AutoUnlockRestrictedSettings : YukiBaseHooker() {
    override fun onHook() {
        val limit = false
        //Source RestrictedPreferenceHelper
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            val member = dexKitBridge.findMethod {
                matcher {
                    paramCount(0)
                    returnType(BooleanType)
                    addCall {
                        name("performClick")
                        returnType(UnitType)
                    }
                    addUsingField {
                        field { type(ContextClass) }
                        field { type(IntType) }
                        field { type(StringClass) }
                        field { type(BooleanType) }
                    }
                    declaredClass {
                        usingStrings("RestrictedPreferenceHelper")
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findMember").single()
            val fields = dexKitBridge.findField {
                searchPackages(member.className)
                matcher {
                    addReadMethod {
                        name(member.methodName)
                        paramCount(0)
                        returnType(BooleanType)
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findFields", onlyOne = false)
            val appops = dexKitBridge.findField {
                searchPackages(member.className)
                matcher {
                    type(BooleanType)
                    addReadMethod {
                        name(member.methodName)
                        paramCount(0)
                        returnType(BooleanType)
                    }
                    addWriteMethod {
                        paramTypes(BooleanType.name)
                        returnType(BooleanType)
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findField AppOps").single()
            val admin = fields.filter { it.typeName == BooleanType.name }.toMutableList().apply {
                removeIf { it.fieldName == appops.fieldName }
            }.first()
            val uidname = fields.find { it.typeName == IntType.name }?.fieldName ?: "uid"
            val packname = fields.find { it.typeName == StringClass.name }?.fieldName
                ?: "packageName"
            member.className.toClass().apply {
                method { name = member.methodName;emptyParam();returnType = BooleanType }.hook {
                    before {
                        val context = field { type = ContextClass }.get(instance).cast<Context>()
                            ?: return@before
                        val disabledAdmin = field { name = admin.fieldName }.get(instance).boolean()
                        val disabledAppOps = field { name = appops.fieldName }.get(instance)
                            .boolean()
                        if (disabledAdmin) return@before
                        if (disabledAppOps) {
                            val uid = field { name = uidname }.get(instance).int()
                            val packName = field { name = packname }.get(instance).string()
                            context.setMode(uid, packName, limit)
                            method { param(BooleanType);returnType = BooleanType }.get(instance)
                                .call(limit)
                            result = limit
                        }
                    }
                }
            }
        }
    }

    private fun Context.setMode(uid: Int, packName: String, limit: Boolean) {
        val appOps = getSystemService(Context.APP_OPS_SERVICE)
        appOps.current().method { name = "setMode";paramCount = 4 }.call(
            119, uid, packName, if (limit) 1 else 0
        )
    }
}