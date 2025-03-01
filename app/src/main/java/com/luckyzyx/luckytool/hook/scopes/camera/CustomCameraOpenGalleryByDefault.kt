package com.luckyzyx.luckytool.hook.scopes.camera

import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class CustomCameraOpenGalleryByDefault(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val gallery = prefs(ModulePrefs).getString("custom_camera_open_gallery_by_default", "")
        if (gallery.isBlank()) return

        //Source GalleryUtil
        dexKitBridge.findClass {
            matcher {
                addFieldForType(Uri::class.java)
                addFieldForType(BooleanType)
                addMethod { paramCount(0);returnType(StringClass) }
                usingStrings("content://com.color.provider.removableapp", "removableapp")
            }
        }.apply {
            checkDataList("CustomCameraOpenGalleryByDefault Clazz")
            findMethod {
                matcher {
                    paramCount(0)
                    returnType(StringClass)
                    usingStrings("com.oplus.gallery.base")
                }
            }.apply {
                checkDataList("CustomCameraOpenGalleryByDefault Method")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        before {
                            if (gallery.isNotBlank()) result = gallery
                        }
                    }
                }
            }
        }
    }
}