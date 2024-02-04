package com.luckyzyx.luckytool.hook.scopes.camera

import android.text.Spanned
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveWatermarkWordLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source CameraSubSettingFragment -> camera_namelength_outofrange -> filter
        //Source CameraSloganSettingFragment -> camera_namelength_outofrange -> filter
        dexKitBridge.findMethod {
            matcher {
                name("filter")
                paramTypes(
                    CharSequenceClass, IntType, IntType,
                    Spanned::class.java, IntType, IntType
                )
                returnType(CharSequenceClass)
                usingStrings("")
                addInvoke {
                    paramCount(2..3)
                    returnType(UnitType)
                }
            }
        }.apply {
            checkDataList("RemoveWatermarkWordLimit")
            single().className.toClass().apply {
                method {
                    name = "filter"
                    param(
                        CharSequenceClass, IntType, IntType,
                        Spanned::class.java, IntType, IntType
                    )
                    returnType = CharSequenceClass
                }.hook {
                    before {
                        result = args().first().cast<CharSequence>() ?: return@before
                    }
                }
            }
        }
    }
}



