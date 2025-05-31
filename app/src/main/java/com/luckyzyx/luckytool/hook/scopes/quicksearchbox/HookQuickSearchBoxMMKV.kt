package com.luckyzyx.luckytool.hook.scopes.quicksearchbox

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookQuickSearchBoxMMKV(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val map = ArrayMap<String, Any>().apply {
            if (prefs(ModulePrefs)
                    .getBoolean("remove_searchbox_uninstalled_app_suggestions", false)
            ) put("new_suggest_app_card", false)
        }
        loadHooker(HookMMKVManager(dexKitBridge, map))
    }

    @Obfuscate
    class HookMMKVManager(val dexKitBridge: DexKitBridge, val map: ArrayMap<String, Any>) :
        YukiBaseHooker() {
        override fun onHook() {
            //Source MMKVManager
            dexKitBridge.findClass {
                matcher {
                    className("com.heytap.quicksearchbox.common.manager.MMKVManager")
                }
            }.apply {
                checkDataList("HookMMKV find clazz")
                findMethod {
                    matcher {
                        paramTypes(StringClass, StringClass)
                        returnType(StringClass)
                        usingStrings("getString")
                    }
                }.apply {
                    checkDataList("HookMMKV find getString")
                    single().className.toClass().apply {
                        method {
                            name = single().methodName
                            param(StringClass, StringClass)
                            returnType = StringClass
                        }.hook {
                            before {
                                val key = args().first().cast<String>()
                                if (key.isNullOrBlank()) return@before
                                when (val value = map[key]) {
                                    null -> return@before
                                    is Boolean -> result = value.toString()
                                    is String -> result = value
                                    is Int -> result = value.toInt()
                                }
                            }
                        }
                    }
                }
                findMethod {
                    matcher {
                        paramTypes(StringClass, BooleanType)
                        returnType(BooleanType)
                        usingStrings("getBoolean")
                    }
                }.apply {
                    checkDataList("HookMMKV find getBoolean")
                    single().className.toClass().apply {
                        method {
                            name = single().methodName
                            param(StringClass, BooleanType)
                            returnType = BooleanType
                        }.hook {
                            before {
                                val key = args().first().cast<String>()
                                if (key.isNullOrBlank()) return@before
                                when (val value = map[key]) {
                                    null -> return@before
                                    "1" -> resultTrue()
                                    "0" -> resultFalse()
                                    "true" -> resultTrue()
                                    "false" -> resultFalse()
                                    is Boolean -> result = value
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}