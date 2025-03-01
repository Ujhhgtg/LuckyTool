package com.luckyzyx.luckytool.hook.scopes.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.type.android.BroadcastReceiverClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.android.PowerManagerClass
import com.highcapable.yukihookapi.hook.type.android.SharedPreferencesClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class LauncherHighTempreatureProtection(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    val key = "LauncherHighTempreatureProtection"
    override fun onHook() {
        //Source ThermalHandler high_temperature_shutdown_message / high_temperature_dialog_auto
        //Key oplus_settings_hightemp_protect 1004
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(IntType)
                    addForType(ContextClass)
                    addForType(HandlerClass)
                    addForType(PowerManagerClass)
                    addForType(SharedPreferencesClass)
                    addForType(BroadcastReceiverClass)
                }
                methods {
                    add { name("handleMessage") }
                    add { paramTypes(ContextClass) }
                    add { paramTypes(IntType, IntType) }
                }
            }
        }.apply {
            checkDataList("LauncherHighTempreatureProtection")
            single().name.toClass().apply {
                constructor { paramCount = 3 }.hook {
                    intercept()
                }
            }
        }
    }
}