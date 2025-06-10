package com.luckyzyx.luckytool.hook.scopes.battery

import android.database.ContentObserver
import android.os.Handler
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.android.LooperClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBatteryTemperatureControl(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source ThermalControlHandler / ThermalControllHandler
        val handlerClazz = dexKitBridge.findClass {
            matcher {
                addFieldForType(ContextClass)
                addFieldForType(LooperClass)
                addMethod { name("handleMessage") }
                usingStrings("ThermalControllHandler")
            }
        }.apply {
            checkDataList(
                "RemoveBatteryTemperatureControl find ThermalControlHandler",
                isDebug = true
            )
        }.single().name

        //Source ThermalControllerCenter
        dexKitBridge.findClass {
            matcher {
                usingStrings("ThermalControllerCenter")
            }
        }.apply {
            checkDataList(
                "RemoveBatteryTemperatureControl find ThermalControllerCenter",
                isDebug = true
            )

            single().name.toClass().apply {
                constructor { param(ContextClass) }.hook {
                    after {
                        val handler = field { type = handlerClazz }.get(instance).cast<Handler>()
                            ?: return@after
                        val newHandler = Handler(handler.looper)
                        field { type = handlerClazz }.get(instance).set(newHandler)
                    }
                }
                method { param(LooperClass) }.hookAll {
                    intercept()
                }
            }
        }

        //Source ThermalControlMonitor
        dexKitBridge.findClass {
            matcher {
                usingStrings("ThermalControlMonitor")
            }
        }.apply {
            checkDataList(
                "RemoveBatteryTemperatureControl find ThermalControlMonitor",
                isDebug = true
            )

            findMethod {
                matcher {
                    paramCount(0)
                    returnType(UnitType)
                    usingFields {
                        add { type(BooleanType) }
                        add { type(HandlerClass) }
                        add { type(ContentResolverClass) }
                        add { type(ContentObserver::class.java) }
                    }
                    addInvoke {
                        paramCount(0)
                        returnType(UnitType)
                    }
                }
            }.apply {
                checkDataList("RemoveBatteryTemperatureControl find startMonitor", isDebug = true)

                single().className.toClass().apply {
                    method { name = single().name;emptyParam() }.hook {
                        intercept()
                    }
                }
            }
        }

        //Source ThermalControlUtils
        "com.oplus.thermalcontrol.ThermalControlUtils".toClass().apply {
            method { param(LooperClass) }.hook {
                intercept()
            }
        }
    }
}