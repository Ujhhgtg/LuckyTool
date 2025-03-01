@file:Suppress("ConstPropertyName")

package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.view.LayoutInflater
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import java.util.function.Supplier

@Obfuscate
object LockScreenComponentStyle : YukiBaseHooker() {
    override fun onHook() {
        if (SDK == A14) loadHooker(LockScreenComponentStyleV14)
        if (SDK < A14) loadHooker(LockScreenComponentStyleV13)
        if (prefs(ModulePrefs).getBoolean("force_display_clock_style_options", false)) {
            if (SDK == A13) loadHooker(ForceDisplayClockStyleOptionsV13)
        }
    }

    @Obfuscate
    object LockScreenComponentStyleV14 : YukiBaseHooker() {
        private const val singleClockProvider =
            "com.oplus.systemui.shared.clocks.SingleClockProvider" //C14
        private const val dualClockProvider =
            "com.oplus.systemui.shared.clocks.DualClockProvider" //C14
        private const val redHorizontalSingleClockProvider =
            "com.oplus.systemui.shared.clocks.RedHorizontalSingleClockProvider" //C14
        private const val redHorizontalDualClockProvider =
            "com.oplus.systemui.shared.clocks.RedHorizontalDualClockProvider" //C14
        private const val sysuiColorExtractor =
            "com.android.systemui.colorextraction.SysuiColorExtractor" //C14
        private val clockSettings = VariousClass(
            "com.android.systemui.plugins.ClockSettings", //C14
            "com.android.systemui.plugins.clocks.ClockSettings"  //C15
        )

        override fun onHook() {
            val mode = prefs(ModulePrefs).getString("lock_screen_custom_clock_component_style", "0")

            //Source ClockRegistry lock_screen_custom_clock_face
            "com.android.systemui.shared.clocks.ClockRegistry".toClass().apply {
                method { name = "getSettings" }.hook {
                    after {
                        if (mode == "0") return@after
                        val res = result<Any>() ?: return@after
                        val clockId = res.current().method { name = "getClockId" }.invoke<String>()
                            ?: return@after
                        val isSingle = !clockId.contains("DualClock")
                        val provider = when (mode) {
                            "1" -> if (isSingle) singleClockProvider else dualClockProvider
                            "2" -> if (isSingle) redHorizontalSingleClockProvider else redHorizontalDualClockProvider
                            else -> return@after
                        }
                        provider.toClassOrNull() ?: return@after
                        result = clockSettings.toClass().buildOf(provider, null) {
                            param(StringClass, IntClass)
                        }
                    }
                }
            }

            //Source ClockSwitchHelper
            "com.oplus.systemui.keyguard.clock.ClockSwitchHelper".toClass().apply {
                method { name = "buildAllClockProviders" }.hook {
                    before {
                        if (mode == "0") return@before
                        val context = field { name = "mContext" }.get(instance).cast<Context>()
                            ?: return@before
                        val layoutInflater = LayoutInflater.from(context)
                        val colorExtractor = args().first().any() ?: return@before
                        val singleClock = singleClockProvider.toClassOrNull()
                            ?.buildOf(context, layoutInflater, colorExtractor) {
                                param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                            }
                        val dualClock = dualClockProvider.toClassOrNull()
                            ?.buildOf(context, layoutInflater, colorExtractor) {
                                param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                            }
                        val redHorizontalSingleClock =
                            redHorizontalSingleClockProvider.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                                }
                        val redHorizontalDualClock = redHorizontalDualClockProvider.toClassOrNull()
                            ?.buildOf(context, layoutInflater, colorExtractor) {
                                param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                            }
                        val list = arrayListOf(
                            singleClock, dualClock,
                            redHorizontalSingleClock, redHorizontalDualClock
                        ).apply {
                            removeIf { it == null }
                            if (isEmpty()) {
                                YLog.error("Clock Providers is empty!")
                                return@before
                            }
                        }
                        result = ArrayList(list)
                    }
                }
            }
        }
    }

    @Obfuscate
    object LockScreenComponentStyleV13 : YukiBaseHooker() {
        private const val singleClockController =
            "com.oplusos.systemui.keyguard.clock.SingleClockController"
        private const val dualClockController =
            "com.oplusos.systemui.keyguard.clock.DualClockController"
        private const val redHorizontalSingleClockController =
            "com.oplusos.systemui.keyguard.clock.RedHorizontalSingleClockController"
        private const val redHorizontalDualClockController =
            "com.oplusos.systemui.keyguard.clock.RedHorizontalDualClockController"
        private const val sysuiColorExtractor =
            "com.android.systemui.colorextraction.SysuiColorExtractor"

        override fun onHook() {
            val mode = prefs(ModulePrefs).getString("lock_screen_custom_clock_component_style", "0")

            //Source SettingsWrapper lock_screen_custom_clock_face
            "com.android.keyguard.clock.SettingsWrapper".toClass().apply {
                method { name = "getLockScreenCustomClockFace" }.hook {
                    after {
                        if (mode == "0") return@after
                        val res = result<String>() ?: return@after
                        val controller = if (res.contains("DualClock").not()) when (mode) {
                            "1" -> singleClockController
                            "2" -> redHorizontalSingleClockController
                            else -> return@after
                        } else when (mode) {
                            "1" -> dualClockController
                            "2" -> redHorizontalDualClockController
                            else -> return@after
                        }
                        controller.toClassOrNull() ?: return@after
                        result = controller
                    }
                }
            }

            //Source ClockManager
            "com.android.keyguard.clock.ClockManager".toClass().apply {
                constructor { paramCount = 8 }.hook {
                    after {
                        if (mode == "0") return@after
                        val context = args().first().cast<Context>() ?: return@after
                        val layoutInflater = LayoutInflater.from(context)
                        val colorExtractor = args(3).any() ?: return@after
                        val opKeyguardClock = Supplier {
                            method { name = "loadClockByName" }.get(instance).call(
                                "com.oplusos.keyguard.OpKeyguardClockController",
                                layoutInflater, colorExtractor
                            )
                        }
                        val singleClock = Supplier {
                            singleClockController.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                                }
                        }
                        val dualClock = Supplier {
                            dualClockController.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                                }
                        }
                        val redHorizontalSingleClock = Supplier {
                            redHorizontalSingleClockController.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                                }
                        }
                        val redHorizontalDualClock = Supplier {
                            redHorizontalDualClockController.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, sysuiColorExtractor)
                                }
                        }
                        arrayListOf(
                            opKeyguardClock, singleClock, dualClock,
                            redHorizontalSingleClock, redHorizontalDualClock
                        ).apply {
                            if (isEmpty()) {
                                YLog.error("Clock Providers is empty!")
                                return@after
                            }
                            forEach {
                                if (it.get() == null) return@forEach
                                method { name = "addBuiltinClock" }.get(instance).call(it)
                            }
                        }
                    }
                }
            }
        }
    }
}