package com.luckyzyx.luckytool.hook.scopes.screenshot

import android.graphics.Bitmap
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class EnablePNGSaveFormat(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        //Source ImageFileFormat -> JPEG / PNG
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(String::class.java)
                    addForType(Bitmap.CompressFormat::class.java)
                }
                methods {
                    add { name("values") }
                    add { returnType(String::class.java) }
                    add { returnType(Bitmap.CompressFormat::class.java) }
                }
                usingStrings("image/jpeg", "image/png")
            }
        }.apply {
            checkDataList("EnablePNGSaveFormat")
            single().name.toClass().resolve().apply {
                method { returnType = String::class }.hookAll {
                    after {
                        result = when (result<String>()) {
                            "image/jpeg" -> "image/png"
                            ".jpg" -> ".png"
                            else -> return@after
                        }
                    }
                }
                firstMethod { returnType = Bitmap.CompressFormat::class.java }.hook {
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