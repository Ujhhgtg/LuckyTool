package com.luckyzyx.luckytool.hook.utils

import android.content.Context
import android.graphics.ColorFilter
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BackgroundBlurDrawableUtils(val classLoader: ClassLoader?) {
    val clazz = "com.android.internal.graphics.drawable.BackgroundBlurDrawable".toClass(classLoader)
    val aggregatorClazz =
        "com.android.internal.graphics.drawable.BackgroundBlurDrawable\$Aggregator"
            .toClass(classLoader)
    val viewRootClazz = "android.view.ViewRootImpl"

    fun buildAggregator(viewRoot: Any): Any? {
        return aggregatorClazz.buildOf(viewRoot) {
            paramCount = 1
        }
    }

    fun createBackgroundBlurDrawable(ins: Any, context: Context): Any? {
        return aggregatorClazz.method { name = "createBackgroundBlurDrawable" }.get(ins)
            .call(context)
    }

    fun Any.setAlpha(blurRadius: Int) {
        current().method { name = "setAlpha";paramCount = 1 }.call(blurRadius)
    }

    fun Any.setBlurColor(r: Float, g: Float, b: Float, a: Float) {
        current().method { name = "setBlurColor";paramCount = 4 }.call(r, g, b, a)
    }

    fun Any.setBlurRadius(blurRadius: Int) {
        current().method { name = "setBlurRadius";paramCount = 1 }.call(blurRadius)
    }

    fun Any.setColor(color: Int) {
        current().method { name = "setColor";paramCount = 1 }.call(color)
    }

    fun Any.setColorFilter(colorFilter: ColorFilter) {
        current().method { name = "setColorFilter";paramCount = 1 }.call(colorFilter)
    }

    fun Any.setCornerRadius(cornerRadius: Float) {
        current().method { name = "setCornerRadius";paramCount = 1 }.call(cornerRadius)
    }

    fun Any.setCornerRadius(
        cornerRadiusTL: Float, cornerRadiusTR: Float, cornerRadiusBL: Float, cornerRadiusBR: Float
    ) {
        current().method { name = "setCornerRadius";paramCount = 4 }
            .call(cornerRadiusTL, cornerRadiusTR, cornerRadiusBL, cornerRadiusBR)
    }

    fun Any.setVisible(visible: Boolean, restart: Boolean) {
        current().method { name = "setVisible";paramCount = 2 }.call(visible, restart)
    }

}