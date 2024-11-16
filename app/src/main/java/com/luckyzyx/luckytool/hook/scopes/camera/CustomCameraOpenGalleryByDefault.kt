package com.luckyzyx.luckytool.hook.scopes.camera

import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class CustomCameraOpenGalleryByDefault(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val gallery = prefs(ModulePrefs).getString("custom_camera_open_gallery_by_default", "")
        if (gallery.isBlank()) return

        //Source GalleryUtil
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(Uri::class.java)
                    addFieldForType(BooleanType)
                    addMethod { paramCount(0);returnType(StringClass) }
                    addMethod { paramCount(0);returnType(BooleanType) }
                    usingStrings("content://com.color.provider.removableapp", "removableapp")
                }
                paramCount(0)
                returnType(StringClass)
                usingStrings("com.oplus.gallery.base")
            }
        }.apply {
            checkDataList("CustomCameraOpenGalleryByDefault")
            single().className.toClass().apply {
                method {
                    name = single().methodName;emptyParam()
                    returnType = StringClass
                }.hook {
                    after {
                        if (gallery.isNotBlank()) result = gallery
                    }
                }
            }
        }
    }
}