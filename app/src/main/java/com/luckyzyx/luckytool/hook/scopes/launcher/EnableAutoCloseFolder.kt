package com.luckyzyx.luckytool.hook.scopes.launcher

import android.view.ViewGroup
import androidx.core.view.children
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object EnableAutoCloseFolder : YukiBaseHooker() {
    override fun onHook() {
        //Source AbstractFloatingView
        "com.android.launcher3.AbstractFloatingView".toClass().apply {
            method { name = "closeOpenViews";paramCount = 4 }.hook {
                before {
                    val activityContext = args().first().any() ?: return@before
                    val animate = args(1).boolean()
                    val type = args(2).int()

                    val typeFolder = field { name = "TYPE_FOLDER" }.get().int()
                    if ((type and typeFolder) == 0) return@before

                    val dragLayer = activityContext.current().method {
                        name = "getDragLayer";superClass()
                    }.invoke<ViewGroup>() ?: return@before
                    dragLayer.children.forEachIndexed { _, view ->
                        if (!isInstance(view)) return@forEachIndexed
                        val isOfType = view.current().method {
                            name = "isOfType";superClass()
                        }.boolean(typeFolder)
                        if (isOfType) {
                            view.current().method {
                                name = "close";paramCount = 1;superClass()
                            }.call(animate)
                        }
                    }
                }
            }
        }
    }
}