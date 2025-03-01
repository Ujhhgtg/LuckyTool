package com.luckyzyx.luckytool.hook.scopes.keyguardclock

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class KeyGuardcLockRedMode(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val redMode = prefs(ModulePrefs).getString("lock_screen_clock_redone_mode", "0")

        //Source CustomizedTextView -> BrandUtils
        dexKitBridge.findClass {
            matcher {
                addFieldForType(BooleanType)
                usingStrings("ro.oplus.image.system_ext.brand", "ro.oplus.image.system_ext.area")
            }
        }.apply {
            checkDataList("KeyGuardcLockRedMode Clazz")
            findField {
                matcher {
                    type(BooleanType)
                    addReadMethod {
                        paramTypes(IntType, IntType)
                        returnType(UnitType)
                    }
                    addReadMethod {
                        paramTypes(CharSequenceClass)
                        returnType(UnitType)
                    }
                    addReadMethod {
                        paramCount(0)
                        returnType(UnitType)
                    }
                }
            }.apply {
                checkDataList("KeyGuardcLockRedMode Field")
                single().className.toClass(initialize = true).apply {
                    field { name = single().fieldName;type = BooleanType }.get().set(
                        when (redMode) {
                            "1" -> true
                            "2" -> false
                            else -> return
                        }
                    )
                }
            }
        }
    }
}