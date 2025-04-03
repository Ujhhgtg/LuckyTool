package com.luckyzyx.luckytool.ui.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.application.ActivityLifecycleManager
import com.luckyzyx.luckytool.utils.ThemeUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ThemeUtils.isDynamicColorsEnabled(this)) {
            setTheme(R.style.Theme_Luckyzyx_NoActionBar_DynamicColors)
            DynamicColors.applyToActivityIfAvailable(this)
        } else {
            setTheme(R.style.Theme_Luckyzyx_NoActionBar_DefaultColors)
        }

        ThemeUtils.initTheme(this)

        super.onCreate(savedInstanceState)
        ActivityLifecycleManager.registerActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityLifecycleManager.unregisterActivity(this)
    }
}