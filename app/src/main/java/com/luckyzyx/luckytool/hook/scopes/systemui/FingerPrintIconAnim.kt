package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object FingerPrintIconAnim : YukiBaseHooker() {

    private val fpIconType = VariousClass(
        "com.oplusos.systemui.keyguard.onscreenfingerprint.OnScreenFingerprintIcon", //C12
        "com.oplus.systemui.keyguard.finger.onscreenfingerprint.OnScreenFingerprintIcon",  //C13
        "com.oplus.systemui.biometrics.finger.udfps.OnScreenFingerprintIcon" //C14 C15
    )

    override fun onHook() {
        val removeMode = prefs(ModulePrefs).getString("remove_fingerprint_icon_mode", "0")
        val isReplaceIcon = prefs(ModulePrefs).getBoolean("replace_fingerprint_icon_switch", false)
        val iconPath = prefs(ModulePrefs).getString("replace_fingerprint_icon_path", "")

        //Source OnScreenFingerprintUiMech
        VariousClass(
            "com.oplusos.systemui.keyguard.onscreenfingerprint.OnScreenFingerprintOpticalAnimCtrl", //C12
            "com.oplus.systemui.keyguard.finger.onscreenfingerprint.OnScreenFingerprintUiMech", //C13
            "com.oplus.systemui.biometrics.finger.udfps.OnScreenFingerprintUiMach", //C14
            "com.oplus.systemui.biometrics.finger.udfps.OnScreenFingerprintUiMech"  //C15
        ).toClass().apply {
            val hasFadeIn = hasMethod { name = "startFadeInAnimation" }
            val hasFadeOut = hasMethod { name = "startFadeOutAnimation" }
            method { name = "loadAnimDrawables" }.hook {
                if (removeMode == "3") intercept()
                else after {
                    when (removeMode) {
                        "0" -> if (isReplaceIcon) instance.setCustomDrawable(iconPath, true)
                        "1" -> instance.setCustomDrawable(null, true)
                        "2" -> {
                            instance.removePressAnim()
                            if (isReplaceIcon) instance.setCustomDrawable(iconPath, true)
                        }
                    }
                }
            }
            if (hasFadeIn) method { name = "startFadeInAnimation" }.hook {
                if (isReplaceIcon) before {
                    instance.setCustomDrawable(iconPath, false)
                    resultNull()
                } else if (removeMode == "1" || removeMode == "3") intercept()
            }
            if (hasFadeOut) method { name = "startFadeOutAnimation" }.hook {
                if (isReplaceIcon) intercept()
                else if (removeMode == "1" || removeMode == "3") intercept()
            }
        }
    }

    private fun Any.setCustomDrawable(iconPath: String?, update: Boolean) {
        this.current {
            val context = field { type = ContextClass }.cast<Context>() ?: return
            val drawable = if (iconPath.isNullOrBlank()) null
            else BitmapFactory.decodeFile(iconPath).toDrawable(context.resources)
            if (drawable == null) {
                field { name { it.contains("fadeInAnimDrawable", true) } }.setNull()
                field { name { it.contains("adeOutAnimDrawable", true) } }.setNull()
            }
            field { name { it.contains("ImMobileDrawable", true) } }.setNull()
            field { type = fpIconType.toClass() }.cast<ImageView>()?.setImageDrawable(drawable)
            if (update) method { name = "updateFpIconColor";emptyParam() }.call()
        }
    }

    private fun Any.removePressAnim() {
        current().field { name { it.contains("PressedAnimDrawable", true) } }.setNull()
        current().field { name { it.contains("PressedAnimDrawableTmp", true) } }.setNull()
    }
}