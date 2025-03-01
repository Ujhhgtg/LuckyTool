package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object CustomFluidCloudIconBackgroundTransparency : YukiBaseHooker() {
    override fun onHook() {
        var customAlpha =
            prefs(ModulePrefs).getInt("custom_fluid_cloud_icon_background_transparency", -1)
        dataChannel.wait<Int>("custom_fluid_cloud_icon_background_transparency") {
            customAlpha = it
        }

        //Source CapsuleViewBg
        "com.oplus.systemui.plugins.seedling.capsule.CapsuleViewBg".toClass().apply {
            method { name = "onDraw" }.hook {
                before {
                    if (customAlpha < 0) return@before
                    field { name = "customDrawable" }.get(instance).cast<Drawable>()?.apply {
                        alpha = 255 / 10 * customAlpha
                    }
                }
            }
        }
    }
}