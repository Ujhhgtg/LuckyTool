package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.TypefaceClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.IChargerUtils
import com.luckyzyx.luckytool.hook.utils.sysui.BatteryControllerUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.createTextDrawable
import com.luckyzyx.luckytool.utils.getIntProperty
import com.luckyzyx.luckytool.utils.getOSVersionCode
import java.io.StringReader
import java.util.Properties

@Obfuscate
object LockScreenChargingComponent : YukiBaseHooker() {
    override fun onHook() {
        when (getOSVersionCode) {
            in 34..Int.MAX_VALUE -> loadHooker(ChargingComponentC15)
            in 30..33 -> loadHooker(ChargingComponentC14)
            in 26..29 -> loadHooker(ChargingComponentC13)
            else -> loadHooker(ChargingComponentC12)
        }
    }

    val ChargeLevelAndLogoView = "com.oplus.charge.view.ChargeLevelAndLogoView"
    val FrameChargeLevelAndLogoView = "com.oplus.charge.view.FrameChargeLevelAndLogoView"
    val ChargeUtil = "com.oplus.charge.util.ChargeUtil"

    val OplusChargeAnimImpl = "com.oplus.charge.viewmodel.OplusChargeAnimImpl"
    val OplusChargeAnimFlavorOneImpl =
        "com.oplus.systemui.keyguard.charginganim.siphonanim.viewmodel.OplusChargeAnimFlavorOneImpl"
    val ChargeLevelAndLogoFlavorOneView =
        "com.oplus.systemui.keyguard.charginganim.siphonanim.view.ChargeLevelAndLogoFlavorOneView"

    @Obfuscate
    @Suppress("LocalVariableName")
    private object ChargingComponentC15 : YukiBaseHooker() {

        private var oplusCharger: Any? = null

        override fun onHook() {
            var userTypeface =
                prefs(ModulePrefs).getBoolean("lock_screen_charging_use_user_typeface", false)
            dataChannel.wait<Boolean>("lock_screen_charging_use_user_typeface") {
                userTypeface = it
            }
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
            var drawTechnology =
                prefs(ModulePrefs).getBoolean("replace_charging_technology_drawing_style", false)
            dataChannel.wait<Boolean>("replace_charging_technology_drawing_style") {
                drawTechnology = it
            }

            val ChargeUtilCLazz = ChargeUtil.toClass()
            val hasShowWattage = ChargeUtilCLazz.hasMethod { name = "getShowWattage" }
            val hasTechnologyStrForFrameCharge = ChargeUtilCLazz.hasMethod {
                name = "getTechnologyStrForFrameCharge"
            }
            val hasShowWattageForFrameCharge = ChargeUtilCLazz.hasMethod {
                name = "getShowWattageForFrameCharge"
            }

            val ChargeLevelAndLogoView = ChargeLevelAndLogoView.toClass()
            val hasUpdateChargeTechImage = ChargeLevelAndLogoView.hasMethod {
                name = "updateChargeTechImage"
            }

            //Source ChargingLevelAndLogoView
            ChargeLevelAndLogoView.apply {
                method { name = "showCNChargeTechLogo" }.hook {
                    before {
                        when (textLogo) {
                            "1" -> resultTrue()
                            "2" -> resultFalse()
                            else -> return@before
                        }
                    }
                }
                if (hasUpdateChargeTechImage) {
                    method { name = "updateChargeTechImage" }.hook {
                        before {
                            if (!drawTechnology) return@before
                            val viewGroup = instance<ViewGroup>()
                            val chargeTechLogo = field { name = "chargeTechLogo" }.get(instance)
                                .cast<ImageView>() ?: return@before
                            val isWirelessCharge =
                                field { name = "isWirelessCharge" }.get(instance).boolean()
                            val chargerTechnology =
                                field { name = "chargerTechnology" }.get(instance).int()
                            val chargeInfo = getChargeInfo()
                            val usbFastChgType = chargeInfo.getIntProperty("usb_fast_chg_type", 0)
                            val ppsMode = chargeInfo.getIntProperty("battery_ppschg_ing", 0)
                            val text = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                chargerTechnology, usbFastChgType, ppsMode, isWirelessCharge
                            )
                            chargeTechLogo.setImageDrawable(
                                createTextDrawable(viewGroup.context, text)
                            )
                            resultNull()
                        }
                    }
                }
                method { name = "updateChargeAnim" }.hook {
                    after {
                        val oplusChargeInfo = args().last().any() ?: return@after

                        if (showWattage && !hasShowWattage) {
                            val chargeWattageView =
                                field { name = "chargeWattage" }.get(instance).cast<TextView>()
                            val cpaWattage = oplusChargeInfo.current().method {
                                name = "getChargeWattageOrigin"
                            }.int()
                            val wattage = oplusChargeInfo.current().method {
                                name = "getChargeWattage"
                            }.string().toIntOrNull()
                            chargeWattageView?.text = when {
                                wattage == 0 && cpaWattage == 0 -> ""
                                wattage == 0 && cpaWattage != 0 -> "${cpaWattage}W"
                                else -> "${wattage}W"
                            }
                        }

                        if (drawTechnology && !hasUpdateChargeTechImage) {
                            val viewGroup = instance<ViewGroup>()
                            val chargeTechLogo = field { name = "chargeTechLogo" }.get(instance)
                                .cast<ImageView>()
                            val isWirelessCharge =
                                field { name = "isWirelessCharge" }.get(instance).boolean()
                            val chargerTechnology =
                                field { name = "chargerTechnology" }.get(instance).int()
                            val chargeInfo = getChargeInfo()
                            val usbFastChgType = chargeInfo.getIntProperty("usb_fast_chg_type", 0)
                            val ppsMode = chargeInfo.getIntProperty("battery_ppschg_ing", 0)
                            val text = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                chargerTechnology, usbFastChgType, ppsMode, isWirelessCharge
                            )
                            chargeTechLogo?.setImageDrawable(
                                createTextDrawable(viewGroup.context, text)
                            )
                        }
                    }
                }
            }

            //Source FrameChargeLevelAndLogoView
            FrameChargeLevelAndLogoView.toClass().apply {
                val hasShowText = hasMethod { name = "shouldShowTextLogo" }
                if (hasShowText) {
                    method { name = "shouldShowTextLogo" }.hook {
                        before {
                            when (textLogo) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                                else -> return@before
                            }
                        }
                    }
                } else {
                    method { name = "updateTextLogo" }.hook {
                        before {
                            when (textLogo) {
                                "1" -> field { name = "currentLocale" }.get(instance).set("zh-CN")
                                "2" -> field { name = "currentLocale" }.get(instance).set("")
                                else -> return@before
                            }
                        }
                    }
                }
                method { name = "updateChargeAnim" }.hook {
                    after {
                        val oplusChargeInfo = args().last().any() ?: return@after

                        if (showRealTech && !hasTechnologyStrForFrameCharge) {
                            val textLogoView = field { name = "textLogo" }.get(instance)
                                .cast<TextView>()
                            val isWirelessCharge = oplusChargeInfo.current().method {
                                name = "isWirelessCharge"
                            }.boolean()
                            val chargerTechnology = oplusChargeInfo.current().method {
                                name = "getChargerTechnology"
                            }.int()
                            val chargeInfo = getChargeInfo()
                            val usbFastChgType =
                                chargeInfo.getIntProperty("usb_fast_chg_type", 0)
                            val ppsMode = chargeInfo.getIntProperty("battery_ppschg_ing", 0)
                            textLogoView?.text =
                                BatteryControllerUtils(appClassLoader).getTechnologyName(
                                    chargerTechnology, usbFastChgType, ppsMode, isWirelessCharge
                                )
                        }

                        if (showWattage && !hasShowWattageForFrameCharge) {
                            val chargeWattageView =
                                field { name = "chargeWattage" }.get(instance).cast<TextView>()
                            val cpaWattage = oplusChargeInfo.current().method {
                                name = "getChargeWattageOrigin"
                            }.int()
                            val wattage = oplusChargeInfo.current().method {
                                name = "getChargeWattage"
                            }.string().toIntOrNull()
                            chargeWattageView?.text = when {
                                wattage == 0 && cpaWattage == 0 -> ""
                                wattage == 0 && cpaWattage != 0 -> "${cpaWattage}W"
                                else -> "${wattage}W"
                            }
                        }
                    }
                }
            }

            //Source OplusChargeAnimImpl -> ChargeUtil
            ChargeUtilCLazz.apply {
                method {
//                    name = "getChargeLevelTypeFace"
//                    name = "getSansTypeFace"
                    param(ContextClass)
                    returnType = TypefaceClass
                }.hook {
                    after {
                        if (!userTypeface) return@after
                        result = Typeface.DEFAULT_BOLD
                    }
                }
                if (hasShowWattage) {
                    method { name = "getShowWattage";paramCount = 3 }.hook {
                        before {
                            if (!showWattage) return@before
                            val cpaWattage = args().first().int()
                            val wattage = args(1).string().toIntOrNull() ?: return@before
//                        val wattage = args(1).string()
//                        val isWireless = args().last().boolean()
//                        YLog.debug("ChargeUtil getShowWattage -> $origin | $wattage | $isWireless")
                            result = when {
                                wattage == 0 && cpaWattage == 0 -> ""
                                wattage == 0 && cpaWattage != 0 -> "${cpaWattage}W"
                                else -> "${wattage}W"
                            }
                        }
                    }
                }
                if (hasShowWattageForFrameCharge) {
                    method { name = "getShowWattageForFrameCharge";paramCount = 3 }.hook {
                        before {
                            if (!showWattage) return@before
                            val cpaWattage = args().first().int()
                            val wattage = args(1).string().toIntOrNull() ?: return@before
                            result = when {
                                wattage == 0 && cpaWattage == 0 -> ""
                                wattage == 0 && cpaWattage != 0 -> "${cpaWattage}W"
                                else -> "${wattage}W"
                            }
                        }
                    }
                }
                if (hasTechnologyStrForFrameCharge) {
                    method { name = "getTechnologyStrForFrameCharge" }.hook {
                        before {
                            if (!showRealTech) return@before
                            val oplusChargeInfo = args().last().any() ?: return@before
                            val isWirelessCharge = oplusChargeInfo.current().method {
                                name = "isWirelessCharge"
                            }.boolean()
                            val chargerTechnology = oplusChargeInfo.current().method {
                                name = "getChargerTechnology"
                            }.int()

                            val chargeInfo = getChargeInfo()
                            val usbFastChgType = chargeInfo.getIntProperty("usb_fast_chg_type", 0)
                            val ppsMode = chargeInfo.getIntProperty("battery_ppschg_ing", 0)
                            result = BatteryControllerUtils(appClassLoader).getTechnologyName(
                                chargerTechnology, usbFastChgType, ppsMode, isWirelessCharge
                            )
                        }
                    }
                }
            }
        }

        private fun getChargeInfo(): Properties {
            return try {
                val queryChargeInfo = IChargerUtils(appClassLoader).let {
                    if (oplusCharger == null) oplusCharger = it.getInstance()
                    it.queryChargeInfo(oplusCharger)
                }
//        LogUtils.d("getChargeInfo", "queryChargeInfo", queryChargeInfo.toString(), true)
                Properties().apply {
                    if (queryChargeInfo.isNullOrBlank().not()) load(StringReader(queryChargeInfo))
                }
            } catch (e: Exception) {
                YLog.error("StatusBarBatteryInfoNotify -> getChargeInfo", e)
                Properties()
            }
        }

    }

    @Obfuscate
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
            ChargeLevelAndLogoView.toClass().apply {
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
            ChargeUtil.toClass().apply {
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
                method { name = "showTechnology" }.hook {
                    if (showRealTech) replaceToTrue()
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
                            result = BatteryControllerUtils(appClassLoader).getTechnologyNameOld(
                                technology, ppsMode, ismIsWirelessCharge
                            )
                        }
                    }
                }
            }

            //Source OplusChargeAnimImpl
            OplusChargeAnimImpl.toClass().apply {
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
                            result = BatteryControllerUtils(appClassLoader).getTechnologyNameOld(
                                technology, ppsMode, ismIsWirelessCharge
                            )
                        }
                    }
                }
            }

            //Source OplusChargeAnimFlavorOneImpl
            OplusChargeAnimFlavorOneImpl.toClassOrNull()?.apply {
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
                        result = BatteryControllerUtils(appClassLoader).getTechnologyNameOld(
                            technology, ppsMode, ismIsWirelessCharge
                        )
                    }
                }
            }

            //Source ChargeLevelAndLogoFlavorOneView
            ChargeLevelAndLogoFlavorOneView.toClassOrNull()?.apply {
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

    @Obfuscate
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
                                    val ins = it.getInstance(context) ?: return@after
                                    val tech = it.getChargerTechnology(ins)
                                    val pps = it.getPPSMode(ins)
                                    val isWireless = it.isWirelessCharging(ins)
                                    it.getTechnologyNameOld(tech, pps, isWireless)
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
                                    val ins = it.getInstance(context) ?: return@after
                                    val tech = it.getChargerTechnology(ins)
                                    val pps = it.getPPSMode(ins)
                                    val isWireless = it.isWirelessCharging(ins)
                                    it.getTechnologyNameOld(tech, pps, isWireless)
                                }
                        }
                    }
                }
        }
    }

    @Obfuscate
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