package com.luckyzyx.luckytool.hook.scope.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import androidx.core.view.children
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.luckyzyx.luckytool.hook.utils.sysui.ClockSwitchHelper
import com.luckyzyx.luckytool.hook.utils.sysui.LunarHelperUtils
import com.luckyzyx.luckytool.hook.utils.sysui.WeatherInfoParseHelper
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.safeOf
import java.util.Calendar

object LockScreenClock : YukiBaseHooker() {
    var callback: ((key: String, value: Any) -> Unit)? = null

    private lateinit var redMode: String
    private var dualClock = false

    override fun onHook() {
        redMode = prefs(ModulePrefs).getString("lock_screen_clock_redone_mode", "0")
        dataChannel.wait<String>("lock_screen_clock_redone_mode") { redMode = it }
        dualClock = prefs(ModulePrefs).getBoolean("apply_lock_screen_dual_clock_redone", false)
        dataChannel.wait<Boolean>("apply_lock_screen_dual_clock_redone") { dualClock = it }
        val isCenter = prefs(ModulePrefs).getBoolean("set_lock_screen_centered", false)
        val userTypeface =
            prefs(ModulePrefs).getBoolean("lock_screen_clock_use_user_typeface", false)
        var showLunar =
            prefs(ModulePrefs).getBoolean("statusbar_control_center_date_show_lunar", false)
        callback = { key: String, value: Any ->
            when (key) {
                "statusbar_control_center_date_show_lunar" -> showLunar = value as Boolean
            }
        }
        val weatherInfoClazz = WeatherInfoParseHelper(appClassLoader).weatherInfoClazz
        val timeInfoClazz = WeatherInfoParseHelper(appClassLoader).timeInfoClazz

        //OPPO/Realme kgd_single_clock / kgd_dual_clock
        //Source SingleClockView kgd_single_clock
        VariousClass(
            "com.oplusos.systemui.keyguard.clock.SingleClockView", //C13
            "com.oplus.systemui.shared.clocks.SingleClockView" //C14
        ).toClass().apply {
            method { name = "onFinishInflate" }.hook {
                after {
                    if (!isCenter && !userTypeface) return@after
                    instance<ViewGroup>().apply {
                        if (isCenter) {
                            setPadding(0, 20.dp, 0, 0)
                            children.forEachIndexed { _, view ->
                                view.setCenterHorizontally()
                            }
                        }
                        if (userTypeface) allViews.filter { it.javaClass extends TextViewClass }
                            .forEachIndexed { _, view ->
                                (view as TextView).typeface = Typeface.DEFAULT
                            }
                    }
                }
            }
            method { name = "updateKeyguardLandClock" }.hook {
                after {
                    if (isCenter) instance<ViewGroup>().setPadding(0, 20.dp, 0, 0)
                }
            }
            method { name = "updateStandardTime" }.hook {
                after {
                    if (redMode == "0") return@after
                    val mTimeHour = field { name = "mTimeHour" }.get(instance).cast<TextView>()
                        ?: return@after
                    val mHour = field { name = "mHour" }.get(instance).string()
                        .takeIf { e -> e.isNotBlank() } ?: return@after
                    mTimeHour.setClockRed(mHour, redMode)
                }
            }
            method { name = "updateDate" }.hook {
                before {
                    if (!showLunar) return@before
                    val context = instance<View>().context
                    val dateView = field { name = "mDate" }.get(instance).cast<TextView>()
                        ?: return@before
                    val localTimeInfo = WeatherInfoParseHelper(appClassLoader)
                        .getLocalTimeInfo(context)
                    if (localTimeInfo != null) {
                        val dateInfo = localTimeInfo.current().method { name = "getDateInfo" }
                            .invoke<String>()
                        dateView.text = dateInfo
                    }
                    val lunarInfo = LunarHelperUtils(appClassLoader).let {
                        val ins = it.buildInstance(context)
                        it.generateLunarDate(ins)
                    }
                    if (hasField { name = "mTvTraditionalCalendar" }) field {
                        name = "mTvTraditionalCalendar"
                    }.get(instance).cast<TextView>()?.text = lunarInfo
                    resultNull()
                }
            }
        }
        //Source DualClockView kgd_dual_clock
        VariousClass(
            "com.oplusos.systemui.keyguard.clock.DualClockView", //C13
            "com.oplus.systemui.shared.clocks.DualClockView" //C14
        ).toClass().apply {
            method { name = "onFinishInflate" }.hook {
                after {
                    if (!userTypeface) return@after
                    instance<ViewGroup>().allViews.filter { it.javaClass extends TextViewClass }
                        .forEachIndexed { _, view ->
                            (view as TextView).typeface = Typeface.DEFAULT
                        }
                }
            }
            method { param { it.contains(weatherInfoClazz) } }.hookAll {
                after {
                    if (!dualClock) return@after
                    val type: String = method.name.let { s ->
                        if (s.contains("updateLocatedTime")) "LocatedTime"
                        else if (s.contains("updateResidentTime")) "ResidentTime"
                        else return@after
                    }
                    val view = if (args.size > 1) args().first().any() ?: return@after
                    else instance
                    when (type) {
                        "LocatedTime" -> {
                            val mLocatedTimeHour = field { name = "mLocatedTimeHour" }
                                .get(view).cast<TextView>() ?: return@after
                            val mLocatedTimeInfo = field { name = "mLocatedTimeInfo" }
                                .get(view).any() ?: return@after
                            val mHour = mLocatedTimeInfo.current().method { name = "getHour" }
                                .invoke<String>() ?: return@after
                            mLocatedTimeHour.setClockRed(mHour, redMode)
                        }

                        "ResidentTime" -> {
                            val mResidentTimeHour = field { name = "mResidentTimeHour" }
                                .get(view).cast<TextView>() ?: return@after
                            val mResidentTimeInfo = field { name = "mResidentTimeInfo" }
                                .get(view).any() ?: return@after
                            val mHour = mResidentTimeInfo.current().method { name = "getHour" }
                                .invoke<String>() ?: return@after
                            mResidentTimeHour.setClockRed(mHour, redMode)
                        }
                    }
                }
            }
        }
        //OnePlus kgd_red_horizontal_single_clock / kgd_red_horizontal_dual_clock
        //Source RedTextClock
        VariousClass(
            "com.oplusos.systemui.keyguard.clock.RedTextClock", //C13
            "com.oplus.systemui.shared.clocks.RedTextClock" //C14
        ).toClass().apply {
            method { name = "onTimeChanged" }.hook {
                after {
                    if (redMode == "0") return@after
                    val mShouldRunTicker = field { name = "mShouldRunTicker" }.get(instance)
                        .boolean()
                    if (!mShouldRunTicker) return@after
                    val format = field { name = "format" }.get(instance).string()
                    val mTime = field { name = "mTime" }.get(instance).cast<Calendar>()
                        ?: return@after
                    val mTimeHour = instance<TextView>()
                    val mHour = DateFormat.format(format, mTime) as String
                    mTimeHour.setClockRed(mHour, redMode)
                }
            }
        }
        //Source RedHorizontalSingleClockView
        VariousClass(
            "com.oplusos.systemui.keyguard.clock.RedHorizontalSingleClockView", //C13
            "com.oplus.systemui.shared.clocks.RedHorizontalSingleClockView" //C14
        ).toClass().apply {
            method { name = "onFinishInflate" }.hook {
                after {
                    if (!isCenter && !userTypeface) return@after
                    instance<ViewGroup>().apply {
                        if (isCenter) {
                            setPadding(0, 20.dp, 0, 0)
                            children.forEachIndexed { _, view ->
                                view.setCenterHorizontally()
                            }
                        }
                        if (userTypeface) allViews.filter { it.javaClass extends TextViewClass }
                            .forEachIndexed { _, view ->
                                (view as TextView).typeface = Typeface.DEFAULT
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
            method { name = "onFinishInflate" }.hook {
                after {
                    if (!userTypeface) return@after
                    instance<ViewGroup>().allViews.filter { it.javaClass extends TextViewClass }
                        .forEachIndexed { _, view ->
                            (view as TextView).typeface = Typeface.DEFAULT
                        }
                }
            }
            if (hasMethod { param { it.contains(timeInfoClazz) };paramCount = 3 }) {
                method { param { it.contains(timeInfoClazz) };paramCount = 3 }.hookAll {
                    after {
                        if (!dualClock) return@after
                        val type: String = method.name.let { s ->
                            if (s.contains("updateLocateTime")) "LocateTime"
                            else if (s.contains("updateResidentTime")) "ResidentTime"
                            else return@after
                        }
                        val view = if (args.size > 1) args().first().any() ?: return@after
                        else instance
                        when (type) {
                            "LocateTime" -> {
                                val mLocatedTimeHour = view.current().field {
                                    name = "mTvHorizontalLocateClockHour"
                                }.cast<TextView>() ?: return@after
                                val mLocatedTimeInfo = args().last().any() ?: return@after
                                val mHour = mLocatedTimeInfo.current().method { name = "getHour" }
                                    .invoke<String>() ?: return@after
                                mLocatedTimeHour.setClockRed(mHour, redMode)
                            }

                            "ResidentTime" -> {
                                val mResidentTimeHour = view.current().field {
                                    name = "mTvHorizontalResidentClockHour"
                                }.cast<TextView>() ?: return@after
                                val mResidentTimeInfo = args().last().any() ?: return@after
                                val mHour = mResidentTimeInfo.current().method { name = "getHour" }
                                    .invoke<String>() ?: return@after
                                mResidentTimeHour.setClockRed(mHour, redMode)
                            }
                        }
                    }
                }
            } else {
                method { name = "updateLocateTime" }.hook {
                    after {
                        if (!dualClock) return@after
                        val mContext = field { name = "mContext" }.get(instance).cast<Context>()
                            ?: return@after
                        val mLocatedTimeHour = field { name = "mTvHorizontalLocateClockHour" }
                            .get(instance).cast<TextView>() ?: return@after
                        val mLocatedTimeInfo =
                            WeatherInfoParseHelper(appClassLoader).getLocalTimeInfo(mContext)
                                ?: return@after
                        val mHour =
                            mLocatedTimeInfo.current().method { name = "getHour" }.invoke<String>()
                                ?: return@after
                        mLocatedTimeHour.setClockRed(mHour, redMode)
                    }
                }
                method { name = "updateResidentTime" }.hook {
                    after {
                        if (!dualClock) return@after
                        val mContext = field { name = "mContext" }.get(instance).cast<Context>()
                            ?: return@after
                        val mResidentTimeHour = field { name = "mTvHorizontalResidentClockHour" }
                            .get(instance).cast<TextView>() ?: return@after
                        val info = ClockSwitchHelper(appClassLoader).let {
                            it.getInstance(mContext)?.let { its -> it.getResidentWeatherInfo(its) }
                        } ?: WeatherInfoParseHelper(appClassLoader).weatherInfoClazz.buildOf {
                            emptyParam()
                        }
                        val timeZone =
                            info?.current()?.method { name = "getTimeZone" }?.invoke<String>()
                                ?: "0.0"
                        val mResidentTimeInfo =
                            WeatherInfoParseHelper(appClassLoader).getResidentTimeInfo(
                                mContext, timeZone
                            ) ?: return@after
                        val mHour =
                            mResidentTimeInfo.current().method { name = "getHour" }.invoke<String>()
                                ?: return@after
                        mResidentTimeHour.setClockRed(mHour, redMode)
                    }
                }
            }
            method { name = "setTextFont" }.hook {
                if (userTypeface) intercept()
            }
        }
    }

    private fun View.setCenterHorizontally() {
        layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun TextView.setClockRed(format: String, redMode: String) {
        val sp = SpannableStringBuilder(format)
        if (redMode == "1") {
            for (i in format.indices) {
                if (format[i].toString() == "1") {
                    val color = safeOf(Color.parseColor("#c41442")) {
                        context.getColor(
                            resources.getIdentifier(
                                "red_clock_hour_color", "color", packageName
                            )
                        )
                    }
                    sp.setSpan(ForegroundColorSpan(color), i, i + 1, 34)
                }
            }
        }
        text = sp
    }
}
