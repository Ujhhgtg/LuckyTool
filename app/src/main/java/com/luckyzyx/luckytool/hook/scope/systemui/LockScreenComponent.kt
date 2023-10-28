package com.luckyzyx.luckytool.hook.scope.systemui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import androidx.core.view.children
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewGroupClass
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.dp
import java.util.function.Supplier

object LockScreenComponent : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A14) loadHooker(LockScreenComponentStyle)
        else loadHooker(LockScreenComponentStyleC13)
        loadHooker(LockScreenComponentFont)
    }

    object LockScreenComponentStyle : YukiBaseHooker() {
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
        private const val clockSettings = "com.android.systemui.plugins.ClockSettings" //C14
        override fun onHook() {
            val mode = prefs(ModulePrefs).getString("lock_screen_custom_component_style", "0")

            //Source ClockRegistry lock_screen_custom_clock_face
            "com.android.systemui.shared.clocks.ClockRegistry".toClass().apply {
                method { name = "getSettings" }.hook {
                    after {
                        if (mode == "0") return@after
                        val res = result<Any>() ?: return@after
                        val clockId = res.current().method { name = "getClockId" }.invoke<String>()
                            ?: return@after
                        val provider = if (clockId.contains("DualClock").not()) when (mode) {
                            "1" -> singleClockProvider
                            "2" -> redHorizontalSingleClockProvider
                            else -> return@after
                        } else when (mode) {
                            "1" -> dualClockProvider
                            "2" -> redHorizontalDualClockProvider
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

    object LockScreenComponentStyleC13 : YukiBaseHooker() {
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
            val mode = prefs(ModulePrefs).getString("lock_screen_custom_component_style", "0")

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

    object LockScreenComponentFont : YukiBaseHooker() {
        override fun onHook() {
            val isCenter = prefs(ModulePrefs).getBoolean("set_lock_screen_centered", false)
            val userTypeface =
                prefs(ModulePrefs).getBoolean("lock_screen_clock_use_user_typeface", false)

            //Source RedHorizontalSingleClockView
            VariousClass(
                "com.oplusos.systemui.keyguard.clock.RedHorizontalSingleClockView", //C13
                "com.oplus.systemui.shared.clocks.RedHorizontalSingleClockView" //C14
            ).toClass().apply {
                method { name = "onFinishInflate" }.hook {
                    after {
                        if (!isCenter) return@after
                        instance<LinearLayout>().apply {
                            setPadding(0, 20.dp, 0, 0)
                            children.forEachIndexed { _, view ->
                                view.setCenterHorizontally()
                            }
                        }
                    }
                }
                method { name = "setTextFont" }.hook {
                    if (userTypeface) intercept()
                }
            }

            //Source RedHorizontalDualClockView
            VariousClass(
                "com.oplusos.systemui.keyguard.clock.RedHorizontalDualClockView", //C13
                "com.oplus.systemui.shared.clocks.RedHorizontalDualClockView" //C14
            ).toClassOrNull()?.apply {
                method { name = "setTextFont" }.hook {
                    if (userTypeface) intercept()
                }
            }

            //Source SingleClockView
            VariousClass(
                "com.oplusos.systemui.keyguard.clock.SingleClockView", //C13
                "com.oplus.systemui.shared.clocks.SingleClockView" //C14
            ).toClass().apply {
                method { name = "onFinishInflate" }.hook {
                    after {
                        if (!isCenter && !userTypeface) return@after
                        instance<LinearLayout>().apply {
                            if (isCenter) setPadding(0, 20.dp, 0, 0)
                            children.forEachIndexed { _, view ->
                                if (isCenter) view.setCenterHorizontally()
                                if (view.javaClass extends TextViewClass) {
                                    if (userTypeface) (view as TextView).typeface =
                                        Typeface.DEFAULT_BOLD
                                } else {
                                    if (view.javaClass extends ViewGroupClass) {
                                        (view as ViewGroup).children.forEachIndexed { _, view2 ->
                                            if (view2.javaClass extends TextViewClass) {
                                                if (userTypeface) (view2 as TextView).typeface =
                                                    Typeface.DEFAULT_BOLD
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Source DualClockView
            VariousClass(
                "com.oplusos.systemui.keyguard.clock.DualClockView", //C13
                "com.oplus.systemui.shared.clocks.DualClockView" //C14
            ).toClass().apply {
                method { name = "onFinishInflate" }.hook {
                    after {
                        if (!userTypeface) return@after
                        instance<LinearLayout>().allViews.forEachIndexed { _, view ->
                            if (view.javaClass extends TextViewClass) {
                                (view as TextView).typeface = Typeface.DEFAULT_BOLD
                            }
                        }
                    }
                }
            }
        }
    }

    private fun View.setCenterHorizontally() {
        layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }
}