package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.A11
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getCharColor
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.safeOfNull

@Obfuscate
object ControlCenterClockStyle : YukiBaseHooker() {
    override fun onHook() {
        if (SDK == A11) loadHooker(ControlCenterClockStyleA11)
        else loadHooker(ControlCenterClock)
    }

    @Obfuscate
    object ControlCenterClock : YukiBaseHooker() {
        override fun onHook() {
            val showSecond =
                prefs(ModulePrefs).getBoolean("control_center_clock_show_second", false)
            var redOneMode =
                prefs(ModulePrefs).getString("statusbar_control_center_clock_red_one_mode", "0")
            dataChannel.wait<String>("statusbar_control_center_clock_red_one_mode") {
                redOneMode = it
            }
            var colonStyle =
                prefs(ModulePrefs).getString("statusbar_control_center_clock_colon_style", "0")
            dataChannel.wait<String>("statusbar_control_center_clock_colon_style") {
                colonStyle = it
            }

            //Source Clock
            "com.android.systemui.statusbar.policy.Clock".toClass().apply {
                method { name = "setShowSecondsAndUpdate" }.hook {
                    before {
                        val view = instance<TextView>()
                        val clockName = safeOfNull {
                            view.context.resources.getResourceEntryName(view.id)
                        } ?: return@before
                        when (clockName) {
                            "qs_footer_clock" -> {}  //经典模式时钟
                            "oplus_qs_clock" -> {}  //分离模式时钟
                            else -> return@before
                        }
                        if (showSecond) args().first().setTrue()
                    }
                }
            }

            //Source BaseClockExt
            VariousClass(
                "com.oplusos.systemui.ext.BaseClockExt", //C13
                "com.oplus.systemui.common.clock.OplusClockExImpl" //C14
            ).toClass().apply {
                method {
                    name = "setTextWithRedOneStyle"
                    paramCount = 2
                }.hook {
                    after {
                        if (redOneMode == "0" && colonStyle == "0") return@after
                        val view = args().first().cast<TextView>() ?: return@after
                        val clockName = safeOfNull {
                            view.context.resources.getResourceEntryName(view.id)
                        } ?: return@after
                        when (clockName) {
                            "qs_footer_clock" -> {}  //经典模式时钟
                            "oplus_qs_clock" -> {}  //分离模式时钟
                            else -> return@after
                        }
                        val char = args().last().cast<CharSequence>() ?: return@after
                        if (char.isBlank()) return@after
                        setStyle(view, char, colonStyle, redOneMode)
                    }
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setStyle(
        view: TextView, char: CharSequence, colonStyle: String, redStyle: String
    ) {
        val colonMode = if (colonStyle == "1") 1 else if (colonStyle == "2") 2 else 0
        val redMode = if (redStyle == "1") 1 else if (redStyle == "2") 2 else 0
        var sb = StringBuilder(view.text)
        if (colonMode != 0) {
            when (colonMode) {
                1 -> sb = StringBuilder(char)
                2 -> for (i in char.indices) {
                    if (sb[i].toString() == ":") {
                        sb = sb.replace(i, i + 1, "\u200e\u2236")
                    }
                }
            }
        }
        if (redMode != 2) {
            val sp = SpannableString(sb)
            for (i2 in 0 until 2) {
                if (sb[i2].toString() == "1") {
                    when (redMode) {
                        0 -> {
                            val color = getCharColor(view.text)
                            if (color != null) sp.setSpan(
                                ForegroundColorSpan(color),
                                i2, i2 + 1, 0
                            )
                        }

                        1 -> {
                            val color = safeOf(Color.parseColor("#c41442")) {
                                view.context.getColor(
                                    view.resources.getIdentifier(
                                        "red_clock_hour_color", "color", packageName
                                    )
                                )
                            }
                            sp.setSpan(ForegroundColorSpan(color), i2, i2 + 1, 0)
                        }
                    }
                }
            }
            view.setText(sp, TextView.BufferType.SPANNABLE)
        } else view.text = sb
    }

    @Obfuscate
    object ControlCenterClockStyleA11 : YukiBaseHooker() {
        override fun onHook() {
            val showSecond =
                prefs(ModulePrefs).getBoolean("control_center_clock_show_second", false)
            var redOneMode =
                prefs(ModulePrefs).getString("statusbar_control_center_clock_red_one_mode", "0")
            dataChannel.wait<String>("statusbar_control_center_clock_red_one_mode") {
                redOneMode = it
            }

            //Source Clock
            "com.android.systemui.statusbar.policy.Clock".toClass().apply {
                method { name = "setShowSecondsAndUpdate" }.hook {
                    before {
                        val view = instance<TextView>()
                        if (view.context.resources.getResourceEntryName(view.id) != "qs_footer_clock") return@before
                        if (showSecond) args().first().setTrue()
                    }
                }
                method {
                    name = "setTextWithOpStyle"
                    paramCount = 1
                }.hook {
                    after {
                        val view = instance<TextView>()
                        if (view.context.resources.getResourceEntryName(view.id) != "qs_footer_clock") return@after
                        val char = args().first().cast<CharSequence>() ?: return@after
                        setStyle(view, char, "0", redOneMode)
                    }
                }
            }
        }
    }
}