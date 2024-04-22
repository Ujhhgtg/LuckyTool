package com.luckyzyx.luckytool.hook.scopes.uiengine

import android.graphics.Typeface
import android.text.TextPaint
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.TypefaceClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object SetAodTypefaceMode : YukiBaseHooker() {
    override fun onHook() {
        val typefaceMode = prefs(ModulePrefs).getString("set_aod_typeface_mode", "0")
        val applyClock = prefs(ModulePrefs).getBoolean("apply_aod_clock_typeface", false)

        //Source AodTextView
        "com.oplus.egview.widget.AodTextView".toClass().apply {
            constructor { paramCount = 3 }.hook {
                after {
                    val typeface = when (typefaceMode) {
                        "1" -> Typeface.DEFAULT
                        "2" -> Typeface.DEFAULT_BOLD
                        else -> return@after
                    }
                    method { name = "setTypeface";param(TypefaceClass);superClass() }.get(instance)
                        .call(typeface)
                }
            }
        }

        //Source TimeView
        "com.oplus.egview.widget.TimeView".toClass().apply {
            method { name = "setTextWidget";param(StringClass) }.hook {
                after {
                    if (applyClock.not()) return@after
                    val typeface = when (typefaceMode) {
                        "1" -> Typeface.DEFAULT
                        "2" -> Typeface.DEFAULT_BOLD
                        else -> return@after
                    }
                    val mTextPaint = field { name = "mTextPaint";superClass() }.get(instance)
                        .cast<TextPaint>() ?: return@after
                    mTextPaint.typeface = typeface
                    instance<View>().requestLayout()
                }
            }
        }
    }
}