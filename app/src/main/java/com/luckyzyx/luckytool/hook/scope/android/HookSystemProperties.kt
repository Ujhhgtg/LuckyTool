package com.luckyzyx.luckytool.hook.scope.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass

class HookSystemProperties(private val props: Map<String, Any>) : YukiBaseHooker() {
    override fun onHook() {
        if (props.isEmpty()) return
        //Source SystemProperties
        "android.os.SystemProperties".toClass().apply {
            method { name = "get";returnType = StringClass }.hookAll {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    val value = props[key]
                    if (value != null && value is String) result = props[key]
                }
            }
            method { name = "getBoolean";returnType = BooleanType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    val value = props[key]
                    if (value != null && value is Boolean) result = props[key]
                }
            }
            method { name = "getInt";returnType = IntType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    val value = props[key]
                    if (value != null && value is Int) result = props[key]
                }
            }
            method { name = "getLong";returnType = LongType }.hook {
                after {
                    val key = args().first().cast<String>()
                    if (key.isNullOrBlank()) return@after
                    val value = props[key]
                    if (value != null && value is Long) result = props[key]
                }
            }
        }
    }
}