package com.luckyzyx.luckytool.hook.scopes.alarmclock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.RemoteViews
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BitmapClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.android.RemoteViewsClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.safeOfNull
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@Suppress("MayBeConstant", "unused")
object AlarmClockWidget : YukiBaseHooker() {

    private val BaseClockWidget = "com.coloros.widget.smallweather.BaseClockWidget"
    private val OnePlusWidget = "com.coloros.widget.smallweather.OnePlusWidget"
    val OppoWeather = "com.coloros.widget.smallweather.OppoWeather"
    val OppoWeatherSingle = "com.coloros.widget.smallweather.OppoWeatherSingle"
    val OppoWeatherVertical = "com.coloros.widget.smallweather.OppoWeatherVertical"
    val OppoWeatherMultiVertical = "com.coloros.widget.smallweather.OppoWeatherMultiVertical"
    val RealmeWeather = "com.coloros.widget.smallweather.RealmeWeather"

    private lateinit var redMode: String

    override fun onHook() {
        redMode = prefs(ModulePrefs).getString("alarmclock_widget_redone_mode", "0")
        dataChannel.wait<String>("alarmclock_widget_redone_mode") { redMode = it }

        val onePlusWidget = OnePlusWidget.toClassOrNull() ?: return
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            when {
                onePlusWidget.hasMethod {
                    param(StringClass, StringClass);returnType = CharSequenceClass
                } -> loadHooker(AlarmClock12)

                onePlusWidget.hasMethod { returnType(RemoteViewsClass) } ->
                    loadHooker(AlarmClock130(dexKitBridge))

                else -> {
                    val baseClockWidget = BaseClockWidget.toClassOrNull()
                    if (baseClockWidget != null) loadHooker(AlarmClock141)
                    else loadHooker(AlarmClock145(dexKitBridge))
                }
            }
        }
    }

    private class AlarmClock145(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source OnePlusWidget / OppoWeather / OppoWeatherSingle / OppoWeatherVertical
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(Class::class.java)
                        addForType(ContextClass)
                        addForType(BitmapClass)
                        addForType(BooleanType)
                        addForType(IntType)
                    }
                    methods {
                        add { paramCount(0);returnType(IntType) }
                        add {
                            paramTypes(RemoteViewsClass, IntType, StringClass)
                            usingStrings("setTimeZone")
                        }
                        add {
                            paramTypes(RemoteViewsClass, BooleanType, BooleanType)
                            usingStrings(
                                "com.oplus.widget.smallweather.WEATHER_CLICK",
                                "com.oplus.widget.smallweather.RESIDENT_CITY_CLICK"
                            )
                        }
                        add {
                            paramTypes(RemoteViewsClass, IntType, CharSequenceClass)
                            usingStrings("setFormat24Hour", "setFormat12Hour")
                        }
                        add {
                            paramTypes(RemoteViewsClass)
                            usingStrings("com.oplus.widget.smallweather.REFRESH_CLICK")
                        }
                    }
                }
            }.apply {
                checkDataList("AlarmClock14")
                single().name.toClass().hookBaseClock()
            }
        }
    }

    private object AlarmClock141 : YukiBaseHooker() {
        override fun onHook() {
            //Source BaseClockWidget
            BaseClockWidget.toClass().hookBaseClock()
        }
    }

    private class AlarmClock130(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //OnePlusWidget setTextViewText -> local_hour_txt -> SpannableStringBuilder -> CharSequence
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(BooleanType)
                        addForType(HandlerClass)
                    }
                    methods {
                        add { returnType(BooleanType) }
                        add { returnType(HandlerClass) }
                        add { paramTypes(ContextClass) }
                        add { paramTypes(ContextClass, StringClass) }
                        add { paramTypes(ContextClass, StringClass, StringClass) }
                    }
                }
            }.apply {
                checkDataList("AlarmClock13")
                single().name.toClass().apply {
                    method {
                        param { it[0] == ContextClass && it[1] == StringClass }
                        paramCount(2..3)
                    }.hookAll {
                        after {
                            if (redMode == "0") return@after
                            result = when (redMode) {
                                "1" -> result<CharSequence>()?.let { s -> setCharRedOne(s) }
                                "2" -> result<CharSequence>().toString()
                                else -> result
                            }
                        }
                    }
                }
            }
        }
    }

    private object AlarmClock12 : YukiBaseHooker() {
        override fun onHook() {
            //Source OnePlusWidget
            "com.coloros.widget.smallweather.OnePlusWidget".toClass().apply {
                method {
                    param(StringClass, StringClass)
                    returnType = CharSequenceClass
                }.hook {
                    after {
                        if (redMode == "0") return@after
                        result = when (redMode) {
                            "1" -> result<CharSequence>()?.let { setCharRedOne(it) }
                            "2" -> result<CharSequence>().toString()
                            else -> result
                        }
                    }
                }
            }
        }
    }

    private fun setCharRedOne(format: CharSequence): CharSequence {
        val sp = SpannableStringBuilder(format)
        val length = if (format.contains(":")) format.toString().substringBefore(":").length
        else if (format.contains("\u2236")) format.toString().substringBefore("\u2236").length
        else format.length
        for (i in 0 until length) {
            if (format[i].toString() == "1") {
                val colorRes = Color.parseColor("#c41442")
                sp.setSpan(ForegroundColorSpan(colorRes), i, i + 1, 34)
            }
        }
        return sp
    }

    /**
     * 新版时钟V14+替换RemoteViews
     * @receiver Class<*>
     */
    fun Class<*>.hookBaseClock() {
        val noContext = hasField { type = ContextClass }.not()
        method { emptyParam();returnType = RemoteViewsClass }.hookAll {
            after {
                val context = field {
                    type = ContextClass;if (noContext) superClass()
                }.get(instance).cast<Context>() ?: return@after
                val res = result<RemoteViews>() ?: return@after
                val layoutName = safeOfNull {
                    context.resources.getResourceEntryName(res.layoutId)
                } ?: return@after
                val replaceLayoutId = getReplaceLayout(context, layoutName, redMode)
                    ?: return@after
                result = RemoteViews(context.packageName, replaceLayoutId)
            }
        }
    }

    /**
     * 获取要替换的布局
     * @param context Context
     * @param layoutName String
     * @param redMode String
     * @return Int?
     */
    @SuppressLint("DiscouragedApi")
    fun getReplaceLayout(context: Context, layoutName: String, redMode: String): Int? {
        val curRedMode = layoutName.contains("red")
        val replaceLayoutName = when (redMode) {
            "1" -> if (curRedMode) layoutName else getRedLayoutRes(layoutName)
            "2" -> if (curRedMode) getNonRedLayoutRes(layoutName) else layoutName
            else -> return null
        }
        val resId = context.resources.getIdentifier(replaceLayoutName, "layout", packageName)
        return resId.takeIf { it != 0 }
    }

    /**
     * 获取非红一布局
     * @param layoutName String?
     * @return String?
     */
    private fun getNonRedLayoutRes(layoutName: String?): String? {
        return when (layoutName) {
            //OnePlusWidget
            "op_double_clock_red_widget_land_view" -> "op_double_clock_widget_land_view"
            "op_double_clock_red_widget_view" -> "op_double_clock_widget_view"
            "one_plus_red_widget_land_view" -> "one_plus_widget_land_view"
            "one_plus_red_widget_view" -> "one_plus_widget_view"
            "table_op_double_clock_red_widget_land_view" -> "table_op_double_clock_widget_land_view"
            "table_op_double_clock_red_widget_view" -> "table_op_double_clock_widget_view"
            "table_one_plus_red_widget_land_view" -> "table_one_plus_widget_land_view"
            "table_one_plus_red_widget_view" -> "table_one_plus_widget_view"
            //OppoWeather
            "hor_double_clock_red_widget_land_view_t" -> "hor_double_clock_widget_land_view_t"
            "hor_double_clock_red_widget_view_t" -> "hor_double_clock_widget_view_t"
            "hor_single_clock_red_widget_land_view_t" -> "hor_single_clock_widget_land_view_t"
            "hor_single_clock_red_widget_view_t" -> "hor_single_clock_widget_view_t"
            "table_hor_double_clock_red_widget_land_view_t" -> "table_hor_double_clock_widget_land_view_t"
            "table_hor_double_clock_red_widget_view_t" -> "table_hor_double_clock_widget_view_t"
            "table_hor_single_clock_red_widget_land_view_t" -> "table_hor_single_clock_widget_land_view_t"
            "table_hor_single_clock_red_widget_view_t" -> "table_hor_single_clock_widget_view_t"
            //OppoWeatherSingle
            "one_line_double_clock_red_widget_land_view_t" -> "one_line_double_clock_widget_land_view_t"
            "one_line_double_clock_red_widget_view_t" -> "one_line_double_clock_widget_view_t"
            "one_line_hor_single_clock_red_widget_land_view_t" -> "one_line_hor_single_clock_widget_land_view_t"
            "one_line_hor_single_clock_red_widget_view_t" -> "one_line_hor_single_clock_widget_view_t"
            "table_one_line_double_clock_red_widget_land_view_t" -> "table_one_line_double_clock_widget_land_view_t"
            "table_one_line_double_clock_red_widget_view_t" -> "table_one_line_double_clock_widget_view_t"
            "table_one_line_hor_single_clock_red_widget_land_view_t" -> "table_one_line_hor_single_clock_widget_land_view_t"
            "table_one_line_hor_single_clock_red_widget_view_t" -> "table_one_line_hor_single_clock_widget_view_t"
            //OppoWeatherVertical
            "vertical_double_clock_red_widget_land_view_t" -> "vertical_double_clock_widget_land_view_t"
            "vertical_double_clock_red_widget_view_t" -> "vertical_double_clock_widget_view_t"
            "vertical_single_clock_red_widget_land_view_t" -> "vertical_single_clock_widget_land_view_t"
            "vertical_single_clock_red_widget_view_t" -> "vertical_single_clock_widget_view_t"
            "table_vertical_double_clock_red_widget_land_view_t" -> "table_vertical_double_clock_widget_land_view_t"
            "table_vertical_double_clock_red_widget_view_t" -> "table_vertical_double_clock_widget_view_t"
            "table_vertical_single_clock_red_widget_land_view_t" -> "table_vertical_single_clock_widget_land_view_t"
            //"vertical_single_clock_red_widget_view_t" -> "vertical_single_clock_widget_view_t"
            //OppoWeatherMultiVertical
            //"hor_double_clock_red_widget_land_view_t" -> "hor_double_clock_widget_land_view_t"
            //"hor_double_clock_red_widget_view_t" -> "hor_double_clock_widget_view_t"
            //"hor_single_clock_red_widget_land_view_t" -> "hor_single_clock_widget_land_view_t"
            "vertical_multi_clock_red_widget_view_t" -> "vertical_multi_clock_widget_view_t"
            //"table_hor_double_clock_red_widget_land_view_t" -> "table_hor_double_clock_widget_land_view_t"
            //"table_hor_double_clock_red_widget_view_t" -> "table_hor_double_clock_widget_view_t"
            //"table_hor_single_clock_red_widget_land_view_t" -> "table_hor_single_clock_widget_land_view_t"
            "table_vertical_multi_clock_red_widget_view_t" -> "table_vertical_multi_clock_widget_view_t"
            else -> null
        }
    }

    /**
     * 获取红一布局
     * @param layoutName String?
     * @return String?
     */
    private fun getRedLayoutRes(layoutName: String?): String? {
        return when (layoutName) {
            //OnePlusWidget
            "op_double_clock_widget_land_view" -> "op_double_clock_red_widget_land_view"
            "op_double_clock_widget_view" -> "op_double_clock_red_widget_view"
            "one_plus_widget_land_view" -> "one_plus_red_widget_land_view"
            "one_plus_widget_view" -> "one_plus_red_widget_view"
            "table_op_double_clock_widget_land_view" -> "table_op_double_clock_red_widget_land_view"
            "table_op_double_clock_widget_view" -> "table_op_double_clock_red_widget_view"
            "table_one_plus_widget_land_view" -> "table_one_plus_red_widget_land_view"
            "table_one_plus_widget_view" -> "table_one_plus_red_widget_view"
            //OppoWeather
            "hor_double_clock_widget_land_view_t" -> "hor_double_clock_red_widget_land_view_t"
            "hor_double_clock_widget_view_t" -> "hor_double_clock_red_widget_view_t"
            "hor_single_clock_widget_land_view_t" -> "hor_single_clock_red_widget_land_view_t"
            "hor_single_clock_widget_view_t" -> "hor_single_clock_red_widget_view_t"
            "table_hor_double_clock_widget_land_view_t" -> "table_hor_double_clock_red_widget_land_view_t"
            "table_hor_double_clock_widget_view_t" -> "table_hor_double_clock_red_widget_view_t"
            "table_hor_single_clock_widget_land_view_t" -> "table_hor_single_clock_red_widget_land_view_t"
            "table_hor_single_clock_widget_view_t" -> "table_hor_single_clock_red_widget_view_t"
            //OppoWeatherSingle
            "one_line_double_clock_widget_land_view_t" -> "one_line_double_clock_red_widget_land_view_t"
            "one_line_double_clock_widget_view_t" -> "one_line_double_clock_red_widget_view_t"
            "one_line_hor_single_clock_widget_land_view_t" -> "one_line_hor_single_clock_red_widget_land_view_t"
            "one_line_hor_single_clock_widget_view_t" -> "one_line_hor_single_clock_red_widget_view_t"
            "table_one_line_double_clock_widget_land_view_t" -> "table_one_line_double_clock_red_widget_land_view_t"
            "table_one_line_double_clock_widget_view_t" -> "table_one_line_double_clock_red_widget_view_t"
            "table_one_line_hor_single_clock_widget_land_view_t" -> "table_one_line_hor_single_clock_red_widget_land_view_t"
            "table_one_line_hor_single_clock_widget_view_t" -> "table_one_line_hor_single_clock_red_widget_view_t"
            //OppoWeatherVertical
            "vertical_double_clock_widget_land_view_t" -> "vertical_double_clock_red_widget_land_view_t"
            "vertical_double_clock_widget_view_t" -> "vertical_double_clock_red_widget_view_t"
            "vertical_single_clock_widget_land_view_t" -> "vertical_single_clock_red_widget_land_view_t"
            "vertical_single_clock_widget_view_t" -> "vertical_single_clock_red_widget_view_t"
            "table_vertical_double_clock_widget_land_view_t" -> "table_vertical_double_clock_red_widget_land_view_t"
            "table_vertical_double_clock_widget_view_t" -> "table_vertical_double_clock_red_widget_view_t"
            "table_vertical_single_clock_widget_land_view_t" -> "table_vertical_single_clock_red_widget_land_view_t"
            //"vertical_single_clock_red_widget_view_t" -> "vertical_single_clock_widget_view_t"
            //OppoWeatherMultiVertical
            //"hor_double_clock_red_widget_land_view_t" -> "hor_double_clock_widget_land_view_t"
            //"hor_double_clock_red_widget_view_t" -> "hor_double_clock_widget_view_t"
            //"hor_single_clock_red_widget_land_view_t" -> "hor_single_clock_widget_land_view_t"
            "vertical_multi_clock_widget_view_t" -> "vertical_multi_clock_red_widget_view_t"
            //"table_hor_double_clock_red_widget_land_view_t" -> "table_hor_double_clock_widget_land_view_t"
            //"table_hor_double_clock_red_widget_view_t" -> "table_hor_double_clock_widget_view_t"
            //"table_hor_single_clock_red_widget_land_view_t" -> "table_hor_single_clock_widget_land_view_t"
            "table_vertical_multi_clock_widget_view_t" -> "table_vertical_multi_clock_red_widget_view_t"
            else -> null
        }
    }
}
