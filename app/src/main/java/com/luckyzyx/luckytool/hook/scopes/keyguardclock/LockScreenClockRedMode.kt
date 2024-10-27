package com.luckyzyx.luckytool.hook.scopes.keyguardclock

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.ModulePrefs

object LockScreenClockRedMode : YukiBaseHooker() {
    override fun onHook() {
        var redMode = prefs(ModulePrefs).getString("lock_screen_clock_redone_mode", "0")
        dataChannel.wait<String>("lock_screen_clock_redone_mode") { redMode = it }

        //Source CustomizedTextView
        "com.oplus.keyguard.clock.base.widget.CustomizedTextView".toClass().apply {
            method { param(CharSequenceClass);returnType = UnitType }.hook {
                after {
                    if (redMode == "0") return@after
                    val view = instance<TextView>()
                    val text = args().first().cast<CharSequence>() ?: return@after
                    setStyle(view, text, redMode)
                }
            }
            if (hasMethod { param(IntType, IntType);returnType = UnitType }) {
                method { param(IntType, IntType);returnType = UnitType }.hook {
                    after {
                        if (redMode == "0") return@after
                        val view = instance<TextView>()
                        setStyle(view, "1", redMode)
                    }
                }
            }
        }
    }

    private fun setStyle(view: TextView, char: CharSequence, redMode: String) {
        if (view.text.toString() != "1" || char.toString() != "1") return
        when (redMode) {
            "1" -> {
                val spannableStringBuilder = SpannableStringBuilder("1")
                spannableStringBuilder.setSpan(
                    ForegroundColorSpan(Color.parseColor("#E62F2F")),
                    0, 1, 34
                )
                view.text = spannableStringBuilder
            }

            "2" -> view.text = "1"

            else -> return
        }
    }
}