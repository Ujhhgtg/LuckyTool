@file:Suppress("unused")

package com.luckyzyx.luckytool.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

object ThemeUtils {

    fun isDynamicColorsEnabled(context: Context): Boolean {
        val enable = context.getBoolean(SettingsPrefs, "use_dynamic_color", true)
        return enable && DynamicColors.isDynamicColorAvailable()
    }

    fun setDynamicColorsEnabled(context: Context, enabled: Boolean) {
        context.putBoolean(SettingsPrefs, "use_dynamic_color", enabled)
    }

    /**
     * 是否为夜间模式
     */
    val Context.isNightMode get() = isNightMode(resources.configuration)

    /**
     * 是否为夜间模式
     * @param configuration Configuration
     * @return Boolean
     */
    fun isNightMode(configuration: Configuration): Boolean {
        return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * 初始化设置主题模式
     * @param context Context
     */
    fun initTheme(context: Context) {
        val mode = context.getString(SettingsPrefs, "dark_theme", "0")
        when (mode) {
            "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}