package com.luckyzyx.luckytool.hook.statusbar

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.sysui.LunarHelperUtils
import com.luckyzyx.luckytool.utils.A11
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.is24
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.safeOfNull
import java.lang.reflect.Method
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

@Obfuscate
object StatusBarClock : YukiBaseHooker() {

    private val clockMode = prefs(ModulePrefs).getString("statusbar_clock_mode", "0")
    private val isYear = prefs(ModulePrefs).getBoolean("statusbar_clock_show_year", false)
    private val isMonth = prefs(ModulePrefs).getBoolean("statusbar_clock_show_month", false)
    private val isDay = prefs(ModulePrefs).getBoolean("statusbar_clock_show_day", false)
    private val isWeek = prefs(ModulePrefs).getBoolean("statusbar_clock_show_week", false)
    private val isPeriod = prefs(ModulePrefs).getBoolean("statusbar_clock_show_period", false)
    private val isDoubleHour =
        prefs(ModulePrefs).getBoolean("statusbar_clock_show_double_hour", false)
    private val isSecond = prefs(ModulePrefs).getBoolean("statusbar_clock_show_second", false)
    private val isHideSpace = prefs(ModulePrefs).getBoolean("statusbar_clock_hide_spaces", false)
    private val isDoubleRow = prefs(ModulePrefs).getBoolean("statusbar_clock_show_doublerow", false)

    private var clockAlignment =
        prefs(ModulePrefs).getString("statusbar_clock_text_alignment", "center")

    private var singleRowFontSize =
        prefs(ModulePrefs).getInt("statusbar_clock_singlerow_fontsize", 0)
    private var doubleRowFontSize =
        prefs(ModulePrefs).getInt("statusbar_clock_doublerow_fontsize", 0)

    private var customFormat =
        prefs(ModulePrefs).getString("statusbar_clock_custom_format", "HH:mm:ss")
    private var customFontsize = prefs(ModulePrefs).getInt("statusbar_clock_custom_fontsize", 0)

    private val userTypeface = prefs(ModulePrefs).getBoolean("statusbar_clock_user_typeface", false)
    private var useBoldFont =
        prefs(ModulePrefs).getBoolean("statusbar_clock_use_bold_font_style", false)

    private var lunarInstance: Any? = null
    private var newline = ""

    override fun onHook() {
        if (clockMode.isBlank() || clockMode == "0") return
        dataChannel.wait<String>("statusbar_clock_text_alignment") { clockAlignment = it }
        dataChannel.wait<String>("statusbar_clock_custom_format") { customFormat = it }
        dataChannel.wait<Int>("statusbar_clock_custom_fontsize") { customFontsize = it }
        dataChannel.wait<Int>("statusbar_clock_singlerow_fontsize") { singleRowFontSize = it }
        dataChannel.wait<Int>("statusbar_clock_doublerow_fontsize") { doubleRowFontSize = it }
        dataChannel.wait<Boolean>("statusbar_clock_use_bold_font_style") { useBoldFont = it }

        //Source Clock
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            constructor { paramCount = 3 }.hook {
                after {
                    val clockView = instance<TextView>().apply {
                        val clockName = safeOfNull { resources.getResourceEntryName(id) }
                        if (clockName != "clock") return@after
                    }
                    val d: Method = clockView.javaClass.superclass.getDeclaredMethod("updateClock")
                    val r = Runnable {
                        d.isAccessible = true
                        d.invoke(clockView)
                    }

                    class T : TimerTask() {
                        override fun run() {
                            Handler(clockView.context.mainLooper).post(r)
                        }
                    }
                    Timer().schedule(T(), 1000 - System.currentTimeMillis() % 1000, 1000)
                }
            }
            method { name = "getSmallTime";returnType = CharSequenceClass }.hook {
                after {
                    val clockView = instance<TextView>().apply {
                        val clockName = safeOfNull { resources.getResourceEntryName(id) }
                        if (clockName != "clock") return@after
                        initView()
                    }
                    val context = clockView.context
                    val mCalendar = field { name = "mCalendar" }.get(instance).cast<Calendar>()
                    val nowTime = mCalendar?.time ?: Date()
                    result = when (clockMode) {
                        "1" -> getDate(context, nowTime) + newline + getTime(context, nowTime)
                        "2" -> formatDate(getFormat(context, customFormat, nowTime), nowTime)
                        else -> return@after
                    }
                }
            }
        }

        //Source StatClock
        VariousClass(
            "com.oplusos.systemui.statusbar.widget.StatClock", //C12 C13
            "com.oplus.systemui.statusbar.widget.StatClock" //C14
        ).toClass().apply {
            method {
                if (SDK == A11) name = "onConfigChanged"
                if (SDK > A11) name = "onConfigurationChanged"
            }.hook {
                intercept()
            }
            if (getOSVersionCode >= 33) {
                method { name = "onMeasure" }.hook {
                    before {
                        field { name = "mShowSeconds";superClass() }.get(instance).setTrue()
                    }
                }
                method { name = "updateMinWidth" }.hook {
                    before {
                        field { name = "mShowSeconds";superClass() }.get(instance).setTrue()
                    }
                }
            }
        }
    }

    private fun getLunar(context: Context, level: Int = 4): String {
        LunarHelperUtils(context, appClassLoader).apply {
            if (lunarInstance == null) lunarInstance = getInstance(context)
            return generateLunarDate(level)
        }
    }

    private fun TextView.initView() {
        if (userTypeface) typeface = if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        val defaultSize = 12F
        if (clockMode == "1") {
            isSingleLine = !isDoubleRow
            if (isDoubleRow) {
                newline = "\n"
                setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (doubleRowFontSize != 0) doubleRowFontSize.toFloat() else defaultSize
                )
                setLineSpacing(0F, 0.8F)
            } else {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (singleRowFontSize != 0) singleRowFontSize.toFloat() else defaultSize
                )
            }
        } else if (clockMode == "2") {
            val formatList = customFormat.split("\n")
            val rows = formatList.size
            isSingleLine = rows == 1
            setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                if (customFontsize != 0) customFontsize.toFloat() else defaultSize
            )
            if (rows != 1) setLineSpacing(0F, 0.8F)
        }
        gravity = if (isSingleLine) Gravity.CENTER else when (clockAlignment) {
            "left" -> Gravity.START or Gravity.CENTER
            "center" -> Gravity.CENTER
            "right" -> Gravity.END or Gravity.CENTER
            else -> Gravity.CENTER
        }
    }

    private fun getFormat(context: Context, format: String, nowTime: Date): String {
        var finalFormat: String = format
        if (finalFormat.contains("NNNN")) finalFormat = finalFormat.replace(
            "NNNN", getLunar(context, 4)
        )
        if (finalFormat.contains("NNN")) finalFormat = finalFormat.replace(
            "NNN", getLunar(context, 3)
        )
        if (finalFormat.contains("NN")) finalFormat = finalFormat.replace(
            "NN", getLunar(context, 2)
        )
        if (finalFormat.contains("N")) {
            finalFormat = finalFormat.replace("N", getLunar(context, 1))
        }
        if (finalFormat.contains("dddd")) finalFormat = finalFormat.replace("dddd", "dd号")
        if (finalFormat.contains("ddd")) finalFormat = finalFormat.replace("ddd", "d号")
        if (finalFormat.contains("FF")) finalFormat = finalFormat.replace("FF", getPeriod(nowTime))
        if (finalFormat.contains("GG")) finalFormat =
            finalFormat.replace("GG", getDiZhiHour(context, nowTime))
        return finalFormat
    }

    private fun getPeriod(nowTime: Date): String {
        return when (formatDate("HH", nowTime)) {
            "00", "01", "02", "03", "04", "05" -> "凌晨"
            "06", "07", "08", "09", "10", "11" -> "上午"
            "12" -> "中午"
            "13", "14", "15", "16", "17" -> "下午"
            "18" -> "傍晚"
            "19", "20", "21", "22", "23" -> "晚上"
            else -> ""
        }
    }

    private fun getDiZhiHour(context: Context, nowTime: Date): String {
        val diZhiArr = LunarHelperUtils(context, appClassLoader).let {
            if (lunarInstance == null) lunarInstance = it.getInstance(context)
            it.mDiZhi
        }
        if (diZhiArr.isEmpty()) return ""
        if (diZhiArr.size != 12) return ""

        val curHour = when (formatDate("HH", nowTime)) {
            "23", "00" -> diZhiArr[0]
            "01", "02" -> diZhiArr[1]
            "03", "04" -> diZhiArr[2]
            "05", "06" -> diZhiArr[3]
            "07", "08" -> diZhiArr[4]
            "09", "10" -> diZhiArr[5]
            "11", "12" -> diZhiArr[6]
            "13", "14" -> diZhiArr[7]
            "15", "16" -> diZhiArr[8]
            "17", "18" -> diZhiArr[9]
            "19", "20" -> diZhiArr[10]
            "21", "22" -> diZhiArr[11]
            else -> ""
        }.let { if (it.isNotBlank()) it + "时" else "" }
        return curHour
    }

    private fun getDate(context: Context, nowTime: Date): String {
        var dateFormat = ""
        if (isZh(context)) {
            if (isYear) dateFormat += "YY年"
            if (isMonth) dateFormat += "M月"
            if (isDay) dateFormat += "d日"
            if (isWeek) dateFormat += "E"
            if (!isHideSpace && !isDoubleRow) dateFormat += " "
        } else {
            if (isWeek) dateFormat += "E"
            if (!isHideSpace && !isDoubleRow) dateFormat += " "
            if (isMonth) {
                dateFormat += "M"
                if (isDay || isYear) dateFormat += "/"
            }
            if (isDay) {
                dateFormat += "d"
                if (isYear) dateFormat += "/"
            }
            if (isYear) {
                dateFormat += "YY"
            }
            if (!isHideSpace && !isDoubleRow) dateFormat += " "
        }
        return formatDate(dateFormat, nowTime)
    }

    private fun getTime(context: Context, nowTime: Date): String {
        var period: String
        var doubleHour: String
        var timeFormat = ""
        timeFormat += if (context.is24) "HH:mm" else "hh:mm"
        if (isSecond) timeFormat += ":ss"
        timeFormat = formatDate(timeFormat, nowTime)
        if (isPeriod) {
            if (isZh(context)) {
                period = getPeriod(nowTime)
                if (!isHideSpace) period += " "
                timeFormat = period + timeFormat
            } else {
                period = " " + formatDate("a", nowTime)
                timeFormat += period
            }
        }
        if (isDoubleHour) {
            doubleHour = getDiZhiHour(context, nowTime)
            if (!isHideSpace) doubleHour = "$doubleHour "
            timeFormat = doubleHour + timeFormat
        }
        return timeFormat
    }
}