package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookSystemProperties(private val props: Map<String, Any>) : YukiBaseHooker() {
    override fun onHook() {
        if (props.isEmpty()) return
        //Source SystemProperties
        "android.os.SystemProperties".toClass().apply {
            method { name = "get";returnType = StringClass }.hookAll {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    when (val value = props[key]) {
                        null -> return@after
                        is Boolean -> result = value.toString()
                        is String -> result = value
                        is Int -> result = value.toInt()
                    }
                }
            }
            method { name = "getBoolean";returnType = BooleanType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    when (val value = props[key]) {
                        null -> return@after
                        "1" -> resultTrue()
                        "0" -> resultFalse()
                        "true" -> resultTrue()
                        "false" -> resultFalse()
                        is Boolean -> result = value
                    }
                }
            }
            method { name = "getInt";returnType = IntType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    when (val value = props[key]) {
                        null -> return@after
                        is Long -> result = value.toInt()
                        is Int -> result = value
                    }
                }
            }
            method { name = "getLong";returnType = LongType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    when (val value = props[key]) {
                        null -> return@after
                        is Int -> result = value.toLong()
                        is Long -> result = value
                    }
                }
            }
        }
    }
}