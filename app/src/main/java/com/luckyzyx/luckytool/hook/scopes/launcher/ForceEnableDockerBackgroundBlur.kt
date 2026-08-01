package com.luckyzyx.luckytool.hook.scopes.launcher

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.view.isEmpty
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object ForceEnableDockerBackgroundBlur : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusHotseat
        "com.android.launcher3.OplusHotseat".toClass().resolve().apply {
            firstMethod { name = "setDockerBackground" }.hook {
                after {
                    val mShortcutsAndWidgets = firstField {
                        name = "mShortcutsAndWidgets"; superclass()
                    }.of(instance).get<ViewGroup>() ?: return@after
                    if (mShortcutsAndWidgets.isEmpty()) {
                        mShortcutsAndWidgets.setBackgroundResource(0)
                        return@after
                    }
                    val drawable = firstMethod { name = "createBlurDrawable" }.of(instance)
                        .invoke<Drawable>() ?: return@after
                    mShortcutsAndWidgets.background = drawable
                }
            }
        }
    }
}