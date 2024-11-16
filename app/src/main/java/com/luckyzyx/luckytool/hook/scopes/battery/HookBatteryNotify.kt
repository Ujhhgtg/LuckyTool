package com.luckyzyx.luckytool.hook.scopes.battery

import android.app.NotificationManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookBatteryNotify(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Channel high_performance_channel_id 5
        val highPerformance =
            prefs(ModulePrefs).getBoolean("remove_high_performance_mode_notifications", false)
        //Channel PowerConsumptionOptimizationChannel / PowerConsumptionOptimizationChannelLow 17
        //power_consumption_optimization_title
        val highBatteryConsumption =
            prefs(ModulePrefs).getBoolean("remove_app_high_battery_consumption_warning", false)
        //Channel smart_charge_channel_id 20
//        val smartRapidCharge = prefs(ModulePrefs).getBoolean("remove_smart_rapid_charging_notification", false)

        //Source NotifyUtil
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass)
                    addForType(HandlerClass)
                    addForType(NotificationManager::class.java)
                }
                addMethod {
                    paramTypes(StringClass, BooleanType)
                    returnType(UnitType)
                }
                usingStrings("NotifyUtil")
            }
        }.apply {
            checkDataList("HookBatteryNotify NotifyUtil")

            if (highPerformance) {
                findMethod {
                    matcher {
                        paramCount(0)
                        returnType(UnitType)
                        usingStrings("high_performance_channel_id", "ACTION_HIGH_PERFORMANCE")
                        usingNumbers(5)
                    }
                }.apply {
                    checkDataList("HookBatteryNotify HighPerformance")
                    single().className.toClass().apply {
                        method { name = single().methodName;emptyParam() }.hook {
                            intercept()
                        }
                    }
                }
            }

            if (highBatteryConsumption) single().name.toClass().apply {
                method { param(StringClass, BooleanType);returnType = UnitType }.hookAll {
                    intercept()
                }
            }
        }
    }
}