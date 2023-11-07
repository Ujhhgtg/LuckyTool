package com.luckyzyx.luckytool.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Formatter
import java.util.Locale
import java.util.regex.Pattern

/**
 * 格式化Date
 * @param format String
 * @return String 格式
 */
fun formatDate(format: String): String {
    return formatDate(format, null, null)
}

/**
 * 格式化Date
 * @param format String 格式
 * @param param Any 要格式的对象
 * @return String
 */
fun formatDate(format: String, param: Any): String {
    return formatDate(format, param, null)
}

/**
 * 格式化Date
 * @param format String 格式
 * @param param Any? 要格式的对象
 * @param locale Locale? 区域
 * @return String
 */
fun formatDate(format: String, param: Any?, locale: Locale?): String {
    return SimpleDateFormat(format, locale ?: Locale.getDefault()).format(param ?: Date())
}

/**
 * 格式化UTC时间戳
 * @param format String
 * @param timeMillis Long
 * @return String?
 */
fun formatDateTimeMillis(format: String, timeMillis: Long): String? {
    val instant = Instant.ofEpochMilli(timeMillis).atOffset(ZoneOffset.UTC)
    return DateTimeFormatter.ofPattern(format).format(instant)
}

/**
 * 格式化Double
 * @param format String 格式
 * @param param Any 要格式化的对象
 * @return Double
 */
fun formatDouble(format: String, param: Any): Double {
    return Formatter().format(format, param).toString().toDoubleOrNull() ?: 0.0
}

/**
 * 利用正则移除字符串前空格
 * @param string String
 */
fun formatSpace(string: String): String {
    val pattern = Pattern.compile("\\p{Alpha}")
    val matcher = pattern.matcher(string)
    if (!matcher.find()) return string
    return string.substring(matcher.start())
}

/**
 * 格式化文件大小
 * @param size Float
 * @return String
 */
fun formatFileSize(size: Float?): String {
    if (size == null || size.isInfinite() || size.isNaN()) return size.toString()
    return if (size >= (1024 * 1024 * 1024)) {
        DecimalFormat("0.00").format(size / (1024 * 1024 * 1024)).toString() + " GB"
    } else if (size >= (1024 * 1024)) {
        DecimalFormat("0.00").format(size / (1024 * 1024)).toString() + " MB"
    } else if (size >= (1024)) {
        DecimalFormat("0.00").format(size / (1024)).toString() + " KB"
    } else "$size B"
}

/**
 * 截取字符串中的数字
 */
val CharSequence.filterNumber get() = this.replace("\\D".toRegex(), "")

/**
 * 格式化字符串空格
 */
val String.replaceSpace get() = this.replace(" ", "")

/**
 * 格式化空白行
 */
val String.replaceBlankLine: String
    get() {
        val listString = this.replaceSpace
        if (listString.contains("\n").not()) return listString
        val formatList = listString.split("\n").toMutableList().apply {
            removeIf { it.isBlank() }
        }
        var finalString = ""
        formatList.forEachIndexed { index, s ->
            finalString += s
            if (formatList.lastIndex != index) finalString += "\n"
        }
        return finalString
    }

fun formatStringSpace(vararg info: String): String {
    var str = ""
    info.forEachIndexed { index, it ->
        if (it != "\n") {
            if (it.isBlank()) return@forEachIndexed
            if (index > 0 && info[index - 1] != "\n") str += " "
        }
        str += it
    }
    return str
}

fun formatStringLine(vararg info: String): String {
    var str = ""
    info.forEachIndexed { index, it ->
        if (it != "\n") {
            if (it.isBlank()) return@forEachIndexed
            if (index > 0 && info[index - 1] != "\n") str += "\n"
        }
        str += it
    }
    return str
}

/**
 * 格式化农历显示
 * @receiver String
 * @param mode Int
 * @return String
 */
fun String.formatLunar(mode: Int): String {
    return try {
        when (mode) {
            1 -> substring(length - 2)
            2 -> if (length > 8) substring(length - 5)
            else if (length > 4) substring(length - 4)
            else this

            3 -> if (length > 8) substring(length - 7)
            else if (length > 4) substring(length - 6)
            else this

            else -> this
        }
    } catch (e: Exception) {
        LogUtils.e(LogUtils.globalTag, "formatLunar", "$e")
        this
    }
}