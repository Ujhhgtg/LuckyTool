package com.luckyzyx.luckytool.ui.application

import com.google.android.material.color.DynamicColors
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.luckyzyx.luckytool.utils.ThemeUtils

class MyApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()

        applyThemeBasedOnPreferences()
    }


    private fun applyThemeBasedOnPreferences() {
        if (ThemeUtils.isDynamicColorsEnabled(this)) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }

    fun reloadAllActivities() {
        ActivityLifecycleManager.recreateAllActivities()
    }
}


























