package com.luckyzyx.luckytool.hook.scope.systemui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.allViews
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.dp

object LockScreenComponent : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A14) loadHooker(LockScreenComponentStyle)
        loadHooker(LockScreenComponentFont)
    }

    const val SingleClockProvider = "com.oplus.systemui.shared.clocks.SingleClockProvider" //C14
    const val DualClockProvider = "com.oplus.systemui.shared.clocks.DualClockProvider" //C14
    const val RedHorizontalSingleClockProvider =
        "com.oplus.systemui.shared.clocks.RedHorizontalSingleClockProvider" //C14
    const val RedHorizontalDualClockProvider =
        "com.oplus.systemui.shared.clocks.RedHorizontalDualClockProvider" //C14
    const val SysuiColorExtractor = "com.android.systemui.colorextraction.SysuiColorExtractor" //C14
    const val ClockSettings = "com.android.systemui.plugins.ClockSettings" //C14

    object LockScreenComponentStyle : YukiBaseHooker() {
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
                            "1" -> SingleClockProvider
                            "2" -> RedHorizontalSingleClockProvider
                            else -> return@after
                        } else when (mode) {
                            "1" -> DualClockProvider
                            "2" -> RedHorizontalDualClockProvider
                            else -> return@after
                        }
                        provider.toClassOrNull() ?: return@after
                        result = ClockSettings.toClass().buildOf(provider, null) {
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
                        val singleClockProvider = SingleClockProvider.toClassOrNull()
                            ?.buildOf(context, layoutInflater, colorExtractor) {
                                param(ContextClass, LayoutInflaterClass, SysuiColorExtractor)
                            }
                        val dualClockProvider = DualClockProvider.toClassOrNull()
                            ?.buildOf(context, layoutInflater, colorExtractor) {
                                param(ContextClass, LayoutInflaterClass, SysuiColorExtractor)
                            }
                        val redHorizontalSingleClockProvider =
                            RedHorizontalSingleClockProvider.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, SysuiColorExtractor)
                                }
                        val redHorizontalDualClockProvider =
                            RedHorizontalDualClockProvider.toClassOrNull()
                                ?.buildOf(context, layoutInflater, colorExtractor) {
                                    param(ContextClass, LayoutInflaterClass, SysuiColorExtractor)
                                }
                        val list = arrayListOf(
                            singleClockProvider, dualClockProvider,
                            redHorizontalSingleClockProvider, redHorizontalDualClockProvider
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
                            allViews.forEachIndexed { _, view ->
                                if (view.javaClass extends TextViewClass) {
                                    view.setCenterHorizontally()
                                }
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
                            setPadding(0, 20.dp, 0, 0)
                            allViews.forEachIndexed { _, view ->
                                if (view.javaClass extends TextViewClass) {
                                    if (isCenter) view.setCenterHorizontally()
                                    if (userTypeface) (view as TextView).typeface =
                                        Typeface.DEFAULT_BOLD
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
        val parentView = parent as ViewGroup
        if (parentView is RelativeLayout) {
            parentView.setCenterHorizontally()
            return
        }
        layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }
}