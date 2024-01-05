package com.luckyzyx.luckytool.hook.scopes.screenshot

import android.graphics.Bitmap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class EnablePNGSaveFormat(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        //Source ImageFileFormat -> JPEG / PNG
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(StringClass.name)
                    addForType(Bitmap.CompressFormat::class.java.name)
                }
                methods {
                    add { name("values") }
                    add { returnType(StringClass) }
                    add { returnType(Bitmap.CompressFormat::class.java) }
                }
                usingStrings("image/jpeg", "image/png")
            }
        }.apply {
            checkDataList("EnablePNGSaveFormat")
            single().name.toClass().apply {
                method { returnType = StringClass }.hookAll {
                    after {
                        result = when (result<String>()) {
                            "image/jpeg" -> "image/png"
                            ".jpg" -> ".png"
                            else -> return@after
                        }
                    }
                }
                method { returnType = Bitmap.CompressFormat::class.java }.hook {
                    after {
                        result = when (result<Bitmap.CompressFormat>()) {
                            Bitmap.CompressFormat.JPEG -> Bitmap.CompressFormat.PNG
                            else -> return@after
                        }
                    }
                }
            }
        }
    }
}