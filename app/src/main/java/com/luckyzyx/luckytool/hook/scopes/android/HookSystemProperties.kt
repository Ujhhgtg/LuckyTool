package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookSystemProperties(private val props: Map<String, Any>) : YukiBaseHooker() {
    override fun onHook() {
        if (props.isEmpty()) return
        //Source SystemProperties
        "android.os.SystemProperties".toClass().resolve().apply {
            method { name = "get";returnType = String::class }.hookAll {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    when (val value = props[key]) {
                        null -> return@after
                        is Boolean -> result = value.toString()
                        is String -> result = value
                        is Int -> result = value
                    }
                }
            }
            firstMethod { name = "getBoolean";returnType = Boolean::class }.hook {
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
            firstMethod { name = "getInt";returnType = Int::class }.hook {
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
            firstMethod { name = "getLong";returnType = Long::class }.hook {
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