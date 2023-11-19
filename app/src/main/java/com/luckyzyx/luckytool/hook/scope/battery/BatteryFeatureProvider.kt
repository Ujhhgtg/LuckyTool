package com.luckyzyx.luckytool.hook.scope.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class BatteryFeatureProvider(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val openScreenPowerSave = prefs(ModulePrefs).getBoolean("open_screen_power_save", false)
        val openBatteryHealth = prefs(ModulePrefs).getBoolean("open_battery_health", false)
        val performanceModeStandbyOptimization =
            prefs(ModulePrefs).getBoolean("performance_mode_and_standby_optimization", false)
        val openBatteryOptimize = false

        //Source AppFeatureProviderUtils
        dexKitBridge.findClass {
            matcher {
                methods {
                    add {
                        paramTypes(ContentResolverClass, StringClass)
                        returnType(BooleanType)
                    }
                    add {
                        paramTypes(ContentResolverClass, StringClass, IntType)
                        returnType(IntType)
                    }
                    add {
                        paramTypes(
                            ContentResolverClass, StringClass, BooleanType
                        )
                        returnType(BooleanType)
                    }
                    add {
                        paramTypes(ContentResolverClass, StringClass)
                        returnType(ListClass)
                    }
                }
            }
        }.apply {
            checkDataList("BatteryFeatureProvider")
            val member = first()
            member.name.toClass().apply {
                method {
                    //                    name = "isFeatureSupport"
                    param(ContentResolverClass, StringClass)
                    returnType = BooleanType
                }.hook {
                    before {
                        when (args(1).string()) {
                            //屏幕省电
                            "com.oplus.battery.cabc_level_dynamic_enable" -> if (openScreenPowerSave) resultTrue()
                            //电池健康
                            "os.charge.settings.batterysettings.batteryhealth" -> if (openBatteryHealth) resultTrue()
                            //电池引擎优化
                            "com.oplus.battery.life.mode.notificate" -> if (openBatteryOptimize) resultTrue()
                            //性能模式/待机优化
                            "com.android.settings.device_rm" -> if (performanceModeStandbyOptimization) resultTrue()
                        }
                    }
                }
                method {
                    //                    name = "getInt"
                    param(ContentResolverClass, StringClass, IntType)
                    returnType = IntType
                }.hook {
                    before {
                        val array = arrayOf(args(1).cast<String>(), args(2).cast<Int>())
                        //电池引擎优化
                        if (array[0] == "com.oplus.battery.life.mode.notificate" && array[1] == 0) {
                            if (openBatteryOptimize) result = 1
                        }
                    }
                }
                method {
                    //                    name = "getBoolean"
                    param(ContentResolverClass, StringClass, BooleanType)
                    returnType = BooleanType
                }.hook {
                    before {
                        val array = arrayOf(args(1).cast<String>(), args(2).cast<Boolean>())
                        when (array[0]) {
                            //睡眠待机优化
                            "com.oplus.battery.disable_deep_sleep" -> resultFalse()
                        }
                    }
                }
            }
        }

        //res/xml/battery_health_preference.xml
        //BatteryHealthDataPreference
    }
}