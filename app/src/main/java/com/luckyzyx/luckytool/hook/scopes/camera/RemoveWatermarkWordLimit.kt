package com.luckyzyx.luckytool.hook.scopes.camera

import android.text.Spanned
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.SDK
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
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
            val onlyOne = SDK >= A13
            checkDataList("RemoveWatermarkWordLimit", onlyOne)
            if (onlyOne.not() && size == 2) {
                forEach {
                    it.className.toClass().apply {
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
            } else single().className.toClass().apply {
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



