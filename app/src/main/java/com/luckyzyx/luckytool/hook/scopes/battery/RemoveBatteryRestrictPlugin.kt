package com.luckyzyx.luckytool.hook.scopes.battery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBatteryRestrictPlugin(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source PluginSupporter
        //Search loadRestrictPlugin / battery_restrict_plugin
        dexKitBridge.findClass {
            matcher {
                addFieldForType(Context::class.java)
                addFieldForType(String::class.java)
                addMethod { paramTypes(Int::class.java, Bundle::class.java) }
                addMethod { paramTypes(Int::class.java, Intent::class.java) }
                addMethod { usingStrings("loadRestrictPlugin", "battery_restrict_plugin") }
                addMethod { usingStrings("loadConfigPlugin", "battery_secret_plugin") }
                addMethod { usingStrings("onPluginConnected") }
            }
        }.apply {
            checkDataList("PluginSupporter")

            findMethod {
                matcher {
                    usingStrings("loadRestrictPlugin", "battery_restrict_plugin")
                }
            }.apply {
                checkDataList("loadRestrictPlugin")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        parameterCount = single().paramCount
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}