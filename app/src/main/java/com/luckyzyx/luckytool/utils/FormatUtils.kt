@file:Suppress("unused")

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
val String.filterNumber get() = replace("\\D".toRegex(), "")

/**
 * 替换字符串空格
 */
val String.replaceSpace get() = replace(" ", "")

/**
 * 替换字符串空白行
 */
val String.replaceBlankLine: String
    get() {
        if (contains("\n").not()) return this
        val formatList = split("\n")
        return formatStringAuto(formatList, "\n", true)
    }

/**
 * 移除空格与空行
 */
val String.replaceBlankAndLine: String
    get() {
        val listString = replaceSpace
        if (listString.contains("\n").not()) return listString
        val formatList = listString.split("\n")
        return formatStringAuto(formatList, "\n", false)
    }

/**
 * 格式化字符串自动添加文本
 * @param formats List<String?> 要格式化的字符数组
 * @param text String 要自动添加的文本
 * @param allowNull Boolean 允许格式化空字符
 * @param allowRepeat Boolean 允许重复添加文本
 * @return String
 */
fun formatStringAuto(
    formats: List<String?>, text: String,
    allowNull: Boolean = true, allowRepeat: Boolean = true
): String {
    var finalText = ""
    if (formats.isEmpty()) return finalText
    formats.forEachIndexed { index, str ->
        if (allowNull.not() && str.isNullOrBlank()) return@forEachIndexed
        finalText += str
        if (allowRepeat.not() && str == text) return@forEachIndexed
        if (index != formats.lastIndex) finalText += text
    }
    return finalText
}