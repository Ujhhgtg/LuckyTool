package com.luckyzyx.luckytool.hook.scopes.gallery

import android.text.Spanned
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.luckypray.dexkit.DexKitBridge

class GalleryWaterMarkWordDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 30) loadHooker(WaterMarkWordDialog(dexKitBridge))
        else loadHooker(WaterMarkWordLimit(dexKitBridge))
    }

    class WaterMarkWordDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source CustomInfoEditDialogHelper

        }
    }

    class WaterMarkWordLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source CustomInfoEditDialogHelper -> picture_editor_text_watermark_character_limit_toast
            dexKitBridge.findMethod {
                matcher {
                    name("filter")
                    paramTypes(
                        CharSequenceClass, IntType, IntType,
                        Spanned::class.java, IntType, IntType
                    )
                    returnType(CharSequenceClass)
                    usingNumbers(0, 1, 2)
                    usingStrings("")
                }
            }.apply {
                checkDataList("RemoveGalleryWaterMarkWordLimit")
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
}