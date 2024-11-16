package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge
import kotlin.math.max
import kotlin.math.min

@Obfuscate
class RemoveDpiRestartRecovery(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusDensityPreference
        "com.oplus.settings.widget.preference.OplusDensityPreference".toClass().apply {
            method { name = "onPreferenceChange";paramCount = 2 }.hook {
                after {
                    val newValue = args().last().string()
                    val context = method { name = "getContext";superClass() }.get(instance)
                        .invoke<Context>() ?: return@after
                    val displayMetrics = context.applicationContext.resources.displayMetrics
                    val min = min(displayMetrics.widthPixels, displayMetrics.heightPixels) *
                            160 / max(newValue.toInt(), 320)
                    val max = max(min, 120)
                    Settings.Secure.putString(
                        context.contentResolver, "display_density_forced", max.toString()
                    )
                    method { name = "notifyChanged";superClass() }.get(instance).call()
                }
            }
        }

        loadHooker(HookSettingsUtils(dexKitBridge))
    }

    @Obfuscate
    class HookSettingsUtils(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source SettingsUtils
            dexKitBridge.findClass {
                matcher {
                    addMethod {
                        paramTypes(ContextClass, BooleanType)
                    }
                    addMethod {
                        paramTypes(StringClass, IntType, IntType, BooleanType)
                        usingStrings("restoreCompassPhoneDisplayDensity")
                    }
                    addMethod {
                        paramTypes(ContextClass, StringClass, IntType)
                        usingStrings("restorePhoneDisplayDensity")
                    }
                    usingStrings("SettingsUtils")
                }
            }.apply {
                checkDataList("RemoveDpiRestartRecovery Clazz")
                findMethod {
                    matcher {
                        paramTypes(ContextClass, BooleanType)
                        addInvoke {
                            paramTypes(StringClass, IntType, IntType, BooleanType)
                            usingStrings("restoreCompassPhoneDisplayDensity")
                        }
                        addInvoke {
                            paramTypes(ContextClass, StringClass, IntType)
                            usingStrings("restorePhoneDisplayDensity")
                        }
                    }
                }.apply {
                    checkDataList("RemoveDpiRestartRecovery Method")
                    single().className.toClass().apply {
                        method {
                            name = single().methodName
                            param(ContextClass, BooleanType)
                        }.hook {
                            intercept()
                        }
                    }
                }
            }
        }
    }
}