package com.luckyzyx.luckytool.hook.scopes.filemanager

import android.text.InputFilter
import android.widget.EditText
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveWordLimitForCompressFiles(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source CompressConfirmDialog
        dexKitBridge.findClass {
            matcher {
                methods {
                    add { name("onTextChanged") }
                    add { paramTypes(EditText::class.java, InputFilter::class.java) }
                }
                usingStrings("CompressConfirmDialog")
            }
        }.apply {
            checkDataList("CompressConfirmDialog")

            findMethod {
                matcher {
                    paramCount(0)
                    returnType(Int::class.java)
                    usingNumbers(50)
                }
            }.apply {
                checkDataList("MaxCount")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        emptyParameters()
                        returnType = Int::class
                    }.hook {
                        replaceTo(9999)
                    }
                }
            }
        }
    }
}