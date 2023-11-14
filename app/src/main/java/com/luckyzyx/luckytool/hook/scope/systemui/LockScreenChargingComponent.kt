package com.luckyzyx.luckytool.hook.scope.systemui

import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.TypefaceClass
import com.luckyzyx.luckytool.hook.utils.sysui.BatteryControllerUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object LockScreenChargingComponent : YukiBaseHooker() {
    override fun onHook() {
        when (getOSVersionCode) {
            in 30..Int.MAX_VALUE -> loadHooker(ChargingComponentC14)
            in 26..29 -> loadHooker(ChargingComponentC13)
            else -> loadHooker(ChargingComponentC12)
        }
    }

    private object ChargingComponentC14 : YukiBaseHooker() {
        override fun onHook() {
            var userTypeface =
                prefs(ModulePrefs).getBoolean("lock_screen_charging_use_user_typeface", false)
            dataChannel.wait<Boolean>("lock_screen_charging_use_user_typeface") {
                userTypeface = it
            }
//            var warpCharge =
//                prefs(ModulePrefs).getString("set_lock_screen_warp_charging_style", "0")
//            dataChannel.wait<String>("set_lock_screen_warp_charging_style") { warpCharge = it }
            var textLogo =
                prefs(ModulePrefs).getString("set_lock_screen_charging_text_logo_style", "0")
            dataChannel.wait<String>("set_lock_screen_charging_text_logo_style") { textLogo = it }
            var showRealTech =
                prefs(ModulePrefs).getBoolean("lock_screen_show_real_charging_technology", false)
            dataChannel.wait<Boolean>("lock_screen_show_real_charging_technology") {
                showRealTech = it
            }
            var showWattage =
                prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
            dataChannel.wait<Boolean>("force_lock_screen_charging_show_wattage") {
                showWattage = it
            }

            //Source ChargingLevelAndLogoView
            "com.oplus.charge.view.ChargeLevelAndLogoView".toClass().apply {
                method { param(TypefaceClass) }.hook {
                    after {
                        if (!userTypeface) return@after
                        instance<LinearLayout>().allViews.forEach {
                            if (it.javaClass == TextViewClass) {
                                (it as TextView).typeface = Typeface.DEFAULT_BOLD
                            }
                        }
                    }
                }
                method { name = "showTextLogo" }.hook {
                    before {
                        when (textLogo) {
                            "1" -> resultTrue()
                            "2" -> resultFalse()
                            else -> return@before
                        }
                    }
                }
            }

            //Source OplusChargeAnimImpl -> ChargeUtil
            "com.oplus.charge.util.ChargeUtil".toClass().apply {
                method { name = "showWattage" }.hook {
                    before {
                        if (!showWattage) return@before
                        val chargeInfoObserver = args().first().any() ?: return@before
                        val getChargeWattage = chargeInfoObserver.current().method {
                            name = "getChargeWattage";emptyParam()
                        }.invoke<String>()?.toIntOrNull() ?: return@before
                        if (getChargeWattage != 0) resultTrue()
                    }
                }
                if (hasMethod { name = "getTechnologyStr" }) {
                    method { name = "getTechnologyStr" }.hook {
                        before {
                            if (!showRealTech) return@before
                            val chargeInfoObserver = args().last().any() ?: return@before
                            val technology = chargeInfoObserver.current().method {
                                name = "getmChargerTechnology"
                            }.invoke<Int>() ?: return@before
                            val ppsMode = chargeInfoObserver.current().method {
                                name = "getmPpsState"
                            }.invoke<Int>() ?: return@before
                            val ismIsWirelessCharge = chargeInfoObserver.current().method {
                                name = "ismIsWirelessCharge"
                            }.invoke<Boolean>() ?: return@before
                            if (ismIsWirelessCharge) return@before
                            result = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                technology, ppsMode
                            )
                        }
                    }
                }
            }

            //Source OplusChargeAnimImpl
            "com.oplus.charge.viewmodel.OplusChargeAnimImpl".toClass().apply {
                if (hasMethod { name = "getTechnologyStr" }) {
                    method { name = "getTechnologyStr" }.hook {
                        before {
                            if (!showRealTech) return@before
                            val chargeInfoObserver = args().last().any() ?: return@before
                            val technology = chargeInfoObserver.current().method {
                                name = "getmChargerTechnology"
                            }.invoke<Int>() ?: return@before
                            val ppsMode = chargeInfoObserver.current().method {
                                name = "getmPpsState"
                            }.invoke<Int>() ?: return@before
                            val ismIsWirelessCharge = chargeInfoObserver.current().method {
                                name = "ismIsWirelessCharge"
                            }.invoke<Boolean>() ?: return@before
                            if (ismIsWirelessCharge) return@before
                            result = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                technology, ppsMode
                            )
                        }
                    }
                }
            }

            //Source OplusChargeAnimFlavorOneImpl
            "com.oplus.systemui.keyguard.charginganim.siphonanim.viewmodel.OplusChargeAnimFlavorOneImpl".toClassOrNull()
                ?.apply {
                    method { name = "getTechnologyStr" }.hook {
                        before {
                            if (!showRealTech) return@before
                            val chargeInfoObserver = args().first().any() ?: return@before
                            val technology = chargeInfoObserver.current().method {
                                name = "getmChargerTechnology"
                            }.invoke<Int>() ?: return@before
                            val ppsMode = chargeInfoObserver.current().method {
                                name = "getmPpsState"
                            }.invoke<Int>() ?: return@before
                            val ismIsWirelessCharge = chargeInfoObserver.current().method {
                                name = "ismIsWirelessCharge"
                            }.invoke<Boolean>() ?: return@before
                            if (ismIsWirelessCharge) return@before
                            result = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                technology, ppsMode
                            )
                        }
                    }
                }

            //Source ChargeLevelAndLogoFlavorOneView
            "com.oplus.systemui.keyguard.charginganim.siphonanim.view.ChargeLevelAndLogoFlavorOneView".toClassOrNull()
                ?.apply {
                    method { param(TypefaceClass) }.hook {
                        after {
                            if (!userTypeface) return@after
                            instance<LinearLayout>().allViews.forEach {
                                if (it.javaClass == TextViewClass) {
                                    (it as TextView).typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        }
                    }
                    method { name = "showTextLogo" }.hook {
                        before {
                            when (textLogo) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                                else -> return@before
                            }
                        }
                    }
                }
        }
    }

    private object ChargingComponentC13 : YukiBaseHooker() {
        override fun onHook() {
            var userTypeface =
                prefs(ModulePrefs).getBoolean("lock_screen_charging_use_user_typeface", false)
            dataChannel.wait<Boolean>("lock_screen_charging_use_user_typeface") {
                userTypeface = it
            }
            var warpCharge =
                prefs(ModulePrefs).getString("set_lock_screen_warp_charging_style", "0")
            dataChannel.wait<String>("set_lock_screen_warp_charging_style") {
                warpCharge = it
            }
            var textLogo =
                prefs(ModulePrefs).getString("set_lock_screen_charging_text_logo_style", "0")
            dataChannel.wait<String>("set_lock_screen_charging_text_logo_style") {
                textLogo = it
            }
            var showRealTech =
                prefs(ModulePrefs).getBoolean("lock_screen_show_real_charging_technology", false)
            dataChannel.wait<Boolean>("lock_screen_show_real_charging_technology") {
                showRealTech = it
            }
            var showWattage =
                prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
            dataChannel.wait<Boolean>("force_lock_screen_charging_show_wattage") {
                showWattage = it
            }

            //Source ChargingLevelAndLogoView
            "com.oplusos.systemui.keyguard.charginganim.siphonanim.ChargingLevelAndLogoView".toClass()
                .apply {
                    method { name = "updatePowerFormat" }.hook {
                        after {
                            if (!userTypeface) return@after
                            instance<LinearLayout>().allViews.forEach {
                                if (it.javaClass == TextViewClass) {
                                    (it as TextView).typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        }
                    }
                    method { name = "showTextLogo" }.hook {
                        before {
                            if (warpCharge != "2") return@before
                            when (textLogo) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                                else -> return@before
                            }
                        }
                    }
                    method { name = "updateLogoResource" }.hook {
                        after {
                            if (warpCharge != "2" || !showRealTech) return@after
                            val context = instance<View>().context
                            val showText = method { name = "showTextLogo" }.get(instance)
                                .invoke<Boolean>() ?: return@after
                            val mTextLogo = field { name = "mTextLogo" }.get(instance)
                                .cast<TextView>() ?: return@after
                            if (showText) mTextLogo.text =
                                BatteryControllerUtils(appClassLoader).let {
                                    val ins = it.getInstance(context)
                                    val tech = it.getChargerTechnology(ins)
                                    val pps = it.getPPSMode(ins)
                                    it.getTechnologyName(tech, pps)
                                }
                        }
                    }
                }

            //Source ChargingAnimationImpl
            "com.oplusos.systemui.keyguard.charginganim.ChargingAnimationImpl".toClass().apply {
                method { name = "isMaxWattageMatchs" }.hook {
                    before {
                        if (warpCharge != "2") return@before
                        val mChargerWattage = field { name = "mChargerWattage" }.get(instance).int()
                        if (showWattage && (mChargerWattage != 0)) resultTrue()
                    }
                }
            }

            //Source ChargingLevelAndLogoViewForFlavorOneVfx
            "com.oplusos.systemui.keyguard.charginganim.siphonanim.flavorone.ChargingLevelAndLogoViewForFlavorOneVfx"
                .toClassOrNull()?.apply {
                    method { name = "setTypeface" }.hook {
                        after {
                            if (!userTypeface) return@after
                            instance<LinearLayout>().allViews.forEach {
                                if (it.javaClass == TextViewClass) {
                                    (it as TextView).typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        }
                    }
                    method { name = "showTextLogo" }.hook {
                        before {
                            if (warpCharge != "2") return@before
                            when (textLogo) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                                else -> return@before
                            }
                        }
                    }
                    method { name = "updateLogoResource" }.hook {
                        after {
                            if (warpCharge != "2" || !showRealTech) return@after
                            val context = instance<View>().context
                            val showText = method { name = "showTextLogo" }.get(instance)
                                .invoke<Boolean>() ?: return@after
                            val mTextLogo = field { name = "mTextLogo" }.get(instance)
                                .cast<TextView>() ?: return@after
                            if (showText) mTextLogo.text =
                                BatteryControllerUtils(appClassLoader).let {
                                    val ins = it.getInstance(context)
                                    val tech = it.getChargerTechnology(ins)
                                    val pps = it.getPPSMode(ins)
                                    it.getTechnologyName(tech, pps)
                                }
                        }
                    }
                }
        }
    }

    private object ChargingComponentC12 : YukiBaseHooker() {
        override fun onHook() {
            var userTypeface =
                prefs(ModulePrefs).getBoolean("lock_screen_charging_use_user_typeface", false)
            dataChannel.wait<Boolean>("lock_screen_charging_use_user_typeface") {
                userTypeface = it
            }
            var textLogo =
                prefs(ModulePrefs).getString("set_lock_screen_charging_text_logo_style", "0")
            dataChannel.wait<String>("set_lock_screen_charging_text_logo_style") {
                textLogo = it
            }
            var showWattage =
                prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
            dataChannel.wait<Boolean>("force_lock_screen_charging_show_wattage") {
                showWattage = it
            }

            //Source ChargingLevelAndLogoView
            "com.oplusos.systemui.keyguard.charginganim.siphonanim.ChargingLevelAndLogoView".toClass()
                .apply {
                    method { name = "updatePowerFormat" }.hook {
                        after {
                            if (!userTypeface) return@after
                            instance<LinearLayout>().allViews.forEach {
                                if (it.javaClass == TextViewClass) {
                                    (it as TextView).typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        }
                    }
                    if (hasMethod { name = "isLocaleZhCN" }) {
                        method { name = "isLocaleZhCN" }.hook {
                            before {
                                when (textLogo) {
                                    "1" -> resultTrue()
                                    "2" -> resultFalse()
                                    else -> return@before
                                }
                            }
                        }
                    }
                }

            //Source ChargingAnimationImpl
            "com.oplusos.systemui.keyguard.charginganim.ChargingAnimationImpl".toClass().apply {
                method { name = "isSupportShowWattage" }.hook {
                    if (showWattage) replaceToTrue()
                }
            }
        }
    }
}