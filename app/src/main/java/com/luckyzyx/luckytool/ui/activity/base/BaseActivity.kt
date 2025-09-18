package com.luckyzyx.luckytool.ui.activity.base

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.DynamicColors
import com.highcapable.betterandroid.ui.component.activity.AppBindingActivity
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.application.ActivityLifecycleManager
import com.luckyzyx.luckytool.utils.ThemeUtils

abstract class BaseActivity<VH : ViewBinding> : AppBindingActivity<VH>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ThemeUtils.isDynamicColorsEnabled(this)) {
            setTheme(R.style.Theme_Luckyzyx_NoActionBar_DynamicColors)
            DynamicColors.applyToActivityIfAvailable(this)
        } else {
            setTheme(R.style.Theme_Luckyzyx_NoActionBar_DefaultColors)
        }

        ThemeUtils.initTheme(this)

        systemBars.destroy()
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        ActivityLifecycleManager.registerActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityLifecycleManager.unregisterActivity(this)
    }
}