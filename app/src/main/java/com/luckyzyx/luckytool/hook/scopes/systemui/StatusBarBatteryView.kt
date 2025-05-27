package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.safeOfNull
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class StatusBarBatteryView(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A14) loadHooker(StatusBarPowerStyle)
        else loadHooker(StatusBarPowerStyleC13)
    }

    @Obfuscate
    object StatusBarPowerStyle : YukiBaseHooker() {
        override fun onHook() {
            val removePercent =
                prefs(ModulePrefs).getBoolean("remove_statusbar_battery_percent", false)
            val userTypeface = prefs(ModulePrefs).getBoolean("statusbar_power_user_typeface", false)
            val useBoldFont =
                prefs(ModulePrefs).getBoolean("statusbar_power_use_bold_font_style", false)
            val customFontSize = prefs(ModulePrefs).getInt("statusbar_power_font_size", 0)
            val applyToIcon =
                prefs(ModulePrefs).getBoolean("statusbar_power_apply_to_battery_icon", false)

            //Source BatteryViewBinder
            "com.oplus.systemui.statusbar.pipeline.battery.ui.binder.BatteryViewBinder".toClass()
                .apply {
                    val hasinitView = hasMethod { name = "bind\$initView" }
                    val hasupdateText = hasMethod { name = "updateText" }
                    val hasupdateOldHorizontal = hasMethod { name = "bind\$updateOldHorizontal" }
                    val hasupdatePercentOutView = hasMethod { name = "bind\$updatePercentOutView" }
                    if (hasinitView) {
                        method { name = "bind\$initView" }.hook {
                            after {
                                args.filterIsInstance<TextView>().forEachIndexed { _, view ->
                                    val entryName = safeOfNull {
                                        view.resources.getResourceEntryName(view.id)
                                    }
                                    when (entryName) {
                                        "battery_text" -> if (applyToIcon) view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )

                                        "battery_percentage_view" -> view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (hasupdateText) {
                        method { name = "updateText" }.hook {
                            after {
                                val view = args().first().cast<TextView>() ?: return@after
                                val entryName = safeOfNull {
                                    view.resources.getResourceEntryName(view.id)
                                }
                                when (entryName) {
                                    "battery_text" -> view.handBatteryTextView(
                                        removePercent, userTypeface, useBoldFont, customFontSize
                                    )

                                    "battery_percentage_view" -> view.handBatteryTextView(
                                        removePercent, userTypeface, useBoldFont, customFontSize
                                    )
                                }
                            }
                        }
                    }
                    if (hasupdateOldHorizontal) {
                        method { name = "bind\$updateOldHorizontal" }.hook {
                            after {
                                args.filterIsInstance<TextView>().forEachIndexed { _, view ->
                                    val entryName = safeOfNull {
                                        view.resources.getResourceEntryName(view.id)
                                    }
                                    when (entryName) {
                                        "battery_text" -> view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )

                                        "battery_percentage_view" -> view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (hasupdatePercentOutView) {
                        method { name = "bind\$updatePercentOutView" }.hook {
                            after {
                                args.filterIsInstance<TextView>().forEachIndexed { _, view ->
                                    val entryName = safeOfNull {
                                        view.resources.getResourceEntryName(view.id)
                                    }
                                    when (entryName) {
                                        "battery_text" -> view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )

                                        "battery_percentage_view" -> view.handBatteryTextView(
                                            removePercent, userTypeface, useBoldFont, customFontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    @Obfuscate
    object StatusBarPowerStyleC13 : YukiBaseHooker() {
        override fun onHook() {
            val removePercent =
                prefs(ModulePrefs).getBoolean("remove_statusbar_battery_percent", false)
            val userTypeface = prefs(ModulePrefs).getBoolean("statusbar_power_user_typeface", false)
            val useBoldFont =
                prefs(ModulePrefs).getBoolean("statusbar_power_use_bold_font_style", false)
            val customFontSize = prefs(ModulePrefs).getInt("statusbar_power_font_size", 0)
            val applyToIcon =
                prefs(ModulePrefs).getBoolean("statusbar_power_apply_to_battery_icon", false)

            //Source StatBatteryMeterView
            "com.oplusos.systemui.statusbar.widget.StatBatteryMeterView".toClass().apply {
                method { name = "onConfigChanged" }.hook {
                    after {
                        method { name = "updatePercentText" }.get(instance).call()
                    }
                }
                method { name = "updatePercentText" }.hook {
                    after {
                        if (applyToIcon) field { name = "batteryPercentView" }.get(instance)
                            .cast<TextView>()?.handBatteryTextView(
                                removePercent, userTypeface, useBoldFont, customFontSize
                            )
                        field { name = "batteryPercentText" }.get(instance).cast<TextView>()
                            ?.handBatteryTextView(
                                removePercent, userTypeface, useBoldFont, customFontSize
                            )
                    }
                }
            }
        }
    }

    companion object {
        fun TextView.handBatteryTextView(
            removePercent: Boolean, userTypeface: Boolean, useBoldFont: Boolean, customFontSize: Int
        ) {
            if (removePercent) text = text.toString().replace("%", "")
            if (userTypeface) {
                typeface = if (useBoldFont) Typeface.DEFAULT_BOLD
                else Typeface.DEFAULT
                setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (customFontSize == 0) 12F else customFontSize.toFloat() * 2
                )
            }
        }
    }
}