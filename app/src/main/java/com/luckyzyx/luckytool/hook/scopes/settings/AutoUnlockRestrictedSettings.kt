package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class AutoUnlockRestrictedSettings(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val limit = false
        //Source RestrictedPreferenceHelper
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(Context::class.java)
                    addForType(String::class.java)
                    addForType(Boolean::class.java)
                    addForType(Int::class.java)
                }
                methods {
                    add {
                        paramCount(0)
                        returnType(Boolean::class.java)
                    }
                    add {
                        paramCount(0)
                        returnType(Void.TYPE)
                    }
                    add {
                        paramTypes(Boolean::class.java)
                        returnType(Boolean::class.java)
                    }
                }
                usingStrings("RestrictedPreferenceHelper")
            }
        }.apply {
            checkDataList("AutoUnlockRestrictedSettings findClass")
            val findMethod = findMethod {
                matcher {
                    paramCount(0)
                    returnType(Boolean::class.java)
                    addCaller {
                        name("performClick")
                        returnType(Void.TYPE)
                    }
                    addUsingField {
                        field { type(Context::class.java) }
                        field { type(Int::class.java) }
                        field { type(String::class.java) }
                        field { type(Boolean::class.java) }
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findMethod").single()

            val fields = findField {
                matcher {
                    addReadMethod {
                        name(findMethod.methodName)
                        paramCount(0)
                        returnType(Boolean::class.java)
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findFields", onlyOne = false)

            val appops = findField {
                matcher {
                    type(Boolean::class.java)
                    addReadMethod {
                        name(findMethod.methodName)
                        paramCount(0)
                        returnType(Boolean::class.java)
                    }
                    addWriteMethod {
                        paramTypes(Boolean::class.java.name)
                        returnType(Boolean::class.java)
                    }
                }
            }.checkDataList("AutoUnlockRestrictedSettings findField AppOps").single()

            val admin =
                fields.filter { it.typeName == Boolean::class.java.name }.toMutableList().apply {
                    removeIf { it.fieldName == appops.fieldName }
                }.first()

            val uidname = fields.find { it.typeName == Int::class.java.name }?.fieldName ?: "uid"
            val packname = fields.find { it.typeName == String::class.java.name }?.fieldName
                ?: "packageName"

            findMethod.className.toClass().resolve().apply {
                firstMethod {
                    name = findMethod.methodName
                    emptyParameters()
                    returnType = Boolean::class
                }.hook {
                    before {
                        val context =
                            firstField { type = Context::class }.of(instance).get<Context>()
                                ?: return@before
                        val disabledAdmin =
                            firstField { name = admin.fieldName }.of(instance).get<Boolean>()
                                ?: false
                        val disabledAppOps = firstField { name = appops.fieldName }.of(instance)
                            .get<Boolean>() ?: false
                        if (disabledAdmin) return@before
                        if (disabledAppOps) {
                            val uid = firstField { name = uidname }.of(instance).get<Int>() ?: -1
                            val packName =
                                firstField { name = packname }.of(instance).get<String>() ?: ""
                            context.setMode(uid, packName, limit)
                            firstMethod {
                                parameters(Boolean::class)
                                returnType = Boolean::class
                            }.of(instance).invoke(limit)
                            result = limit
                        }
                    }
                }
            }
        }
    }

    private fun Context.setMode(uid: Int, packName: String, limit: Boolean) {
        val appOps = getSystemService(Context.APP_OPS_SERVICE)
        appOps.resolve().firstMethod { name = "setMode";parameterCount = 4 }.invoke(
            119, uid, packName, if (limit) 1 else 0
        )
    }
}