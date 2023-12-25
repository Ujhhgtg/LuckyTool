package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.luckyzyx.luckytool.hook.statusbar.StatusBarClock.toClass
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.formatDate
import java.util.Date

@Suppress("unused")
class LunarHelperUtils(val classLoader: ClassLoader?) {
    private val tags = "LunarHelperUtils"

    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.LunarHelper",  //C13
        "com.oplus.systemui.keyguard.clock.LunarHelper"  //C14
    ).toClass(classLoader)
    private val oplusDateClazz = "com.oplus.util.OplusChineseDateAndSolarDate"
        .toClass(classLoader, true)

    /**
     * 构建日历对象实例
     * @param context Context
     * @return Any?
     */
    fun buildInstance(context: Context): Any? {
        return clazz.buildOf(context) {
            param(ContextClass)
        }
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
     * 生成农历阴历
     * @param instance Any
     * @return String
     */
    fun generateLunarDate(instance: Any?, time: Long = System.currentTimeMillis()): String? {
        return try {
            val date = Date(time)
            val year = formatDate("yyyy", date).toInt()
            val month = formatDate("MM", date).toInt()
            val day = formatDate("dd", date).toInt()
            val sunDateToChineseDate = oplusDateClazz.method { name = "SunDateToChineseDate" }.get()
                .invoke<IntArray>(year, month, day) ?: IntArray(3)
            val getChLeapMonth = oplusDateClazz.method { name = "GetChLeapMonth" }.get()
                .invoke<Int>(sunDateToChineseDate[0]) ?: 0
            sunDateToChineseDate[1] = clazz.method { name = "adjustLunarMonth" }.get(instance)
                .invoke<Int>(sunDateToChineseDate[1], getChLeapMonth) ?: 0
            val getCyclical = clazz.method { name = "getCyclical" }.get(instance)
                .invoke<String>(sunDateToChineseDate[0])
            val getAnimalsYear = clazz.method { name = "getAnimalsYear" }.get(instance)
                .invoke<String>(sunDateToChineseDate[0])
            val getLeapLunarMonthString = clazz.method { name = "getLeapLunarMonthString" }
                .get(instance)
                .invoke<String>(sunDateToChineseDate[1], getChLeapMonth)
            val getChineseLunarDayString = clazz.method { name = "getChineseLunarDayString" }
                .get(instance)
                .invoke<String>(sunDateToChineseDate[2])
            getCyclical + getAnimalsYear + getLeapLunarMonthString + getChineseLunarDayString
        } catch (e: Exception) {
            LogUtils.e(tags, "generateLunarDate", "$e")
            null
        }
    }
}