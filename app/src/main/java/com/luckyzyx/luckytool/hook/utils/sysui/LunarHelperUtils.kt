package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.luckyzyx.luckytool.hook.statusbar.StatusBarClock.toClass
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.formatDate
import com.oplus.util.OplusChineseDateAndSolarDate
import java.util.Date

@Suppress("unused")
class LunarHelperUtils(val classLoader: ClassLoader?) {
    private val tags = "LunarHelperUtils"

    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.LunarHelper",  //C13
        "com.oplus.systemui.keyguard.clock.LunarHelper"  //C14
    ).toClass(classLoader)

    /**
     * 获取LunarHelper实例
     * @param context Context
     * @return Any?
     */
    fun getInstance(context: Context): Any? {
        return clazz.buildOf(context) { param(ContextClass) }
    }

    /**
     * 调整农历月份
     * @param instance Any?
     * @param i Int
     * @param i2 Int
     * @return Int?
     */
    private fun adjustLunarMonth(instance: Any?, i: Int, i2: Int): Int? {
        return clazz.method {
            name = "adjustLunarMonth"
            param(IntType, IntType)
        }.get(instance).invoke<Int>(i, i2)
    }

    /**
     * 获取动物年
     * @param instance Any?
     * @param i Int
     * @return String?
     */
    private fun getAnimalsYear(instance: Any?, i: Int): String? {
        return clazz.method {
            name = "getAnimalsYear"
            param(IntType)
        }.get(instance).invoke<String>(i)
    }

    /**
     * 获取中国农历日期字符串
     * @param instance Any?
     * @param i Int
     * @return String?
     */
    private fun getChineseLunarDayString(instance: Any?, i: Int): String? {
        return clazz.method {
            name = "getChineseLunarDayString"
            param(IntType)
        }.get(instance).invoke<String>(i)
    }

    /**
     * 获取中国农历月份字符串
     * @param instance Any?
     * @param i Int
     * @return String?
     */
    fun getChineseLunarMonthString(instance: Any?, i: Int): String? {
        return clazz.method {
            name = "getChineseLunarMonthString"
            param(IntType)
        }.get(instance).invoke<String>(i)
    }

    /**
     * 获取周期
     * @param instance Any?
     * @param i Int
     * @return String?
     */
    private fun getCyclical(instance: Any?, i: Int): String? {
        return clazz.method {
            name = "getCyclical"
            param(IntType)
        }.get(instance).invoke<String>(i)
    }

    /**
     * 获取闰月字符串
     * @param instance Any?
     * @param i Int
     * @param i2 Int
     * @return String?
     */
    private fun getLeapLunarMonthString(instance: Any?, i: Int, i2: Int): String? {
        return clazz.method {
            name = "getLeapLunarMonthString"
            param(IntType, IntType)
        }.get(instance).invoke<String>(i, i2)
    }

    /**
     * 获取农历日期
     * @param instance Any?
     * @param i Int
     * @param i2 Int
     * @param i3 Int
     * @return String?
     */
    fun getLunarDate(instance: Any?, i: Int, i2: Int, i3: Int): String? {
        return clazz.method {
            name = "getLunarDate"
            param(IntType, IntType, IntType)
        }.get(instance).invoke<String>(i, i2, i3)
    }

    /**
     * 获取日历字符串
     * @param instance Any
     * @param time Long
     * @return String?
     */
    fun getDateToString(instance: Any?, time: Long = System.currentTimeMillis()): String? {
        return clazz.method {
            name = "getDateToString"
            param(LongType)
        }.get(instance).invoke<String>(time)
    }

    /**
     * 获取天干字符串数组
     * @param instance Any?
     * @return Array<String>?
     */
    fun getTianGan(instance: Any?): Array<String> {
        return clazz.field {
            name = "mGan"
            type = StringArrayClass
        }.get(instance).array<String>()
    }

    /**
     * 获取地支字符串数组
     * @param instance Any?
     * @return Array<String>?
     */
    fun getDiZhi(instance: Any?): Array<String> {
        return clazz.field {
            name = "mZhi"
            type = StringArrayClass
        }.get(instance).array<String>()
    }

    /**
     * 生成农历日期字符串
     * @param instance Any
     * @return String
     */
    @Suppress("LocalVariableName")
    fun generateLunarDate(
        instance: Any?, level: Int = 4, time: Long = System.currentTimeMillis()
    ): String {
        return try {
            val date = Date(time)
            val year = formatDate("yyyy", date).toInt()
            val month = formatDate("MM", date).toInt()
            val day = formatDate("dd", date).toInt()
            val SunDateToChineseDate =
                OplusChineseDateAndSolarDate.SunDateToChineseDate(year, month, day)
            val GetChLeapMonth =
                OplusChineseDateAndSolarDate.GetChLeapMonth(SunDateToChineseDate[0])
            SunDateToChineseDate[1] =
                adjustLunarMonth(instance, SunDateToChineseDate[1], GetChLeapMonth) ?: 0
            val getCyclical = getCyclical(instance, SunDateToChineseDate[0]) ?: ""
            val getAnimalsYear = getAnimalsYear(instance, SunDateToChineseDate[0]) ?: ""
            val getLeapLunarMonthString =
                getLeapLunarMonthString(instance, SunDateToChineseDate[1], GetChLeapMonth) ?: ""
            val getChineseLunarDayString =
                getChineseLunarDayString(instance, SunDateToChineseDate[2]) ?: ""
            val final = when (level) {
                1 -> getChineseLunarDayString
                2 -> getLeapLunarMonthString + getChineseLunarDayString
                3 -> getAnimalsYear + getLeapLunarMonthString + getChineseLunarDayString
                4 -> getCyclical + getAnimalsYear + getLeapLunarMonthString + getChineseLunarDayString
                else -> getCyclical + getAnimalsYear + getLeapLunarMonthString + getChineseLunarDayString
            }
//            LogUtils.d("generateLunarDate", "final", final, true)
            final
        } catch (e: Exception) {
            LogUtils.e(tags, "generateLunarDate", "$e")
            ""
        }
    }
}