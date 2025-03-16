package com.luckyzyx.luckytool.hook.scopes.launcher

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.CanvasClass
import com.highcapable.yukihookapi.hook.type.android.MotionEventClass
import com.luckyzyx.commonutils.safeOfNull
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object PageIndicator : YukiBaseHooker() {
    override fun onHook() {
        val removeDesktop = prefs(ModulePrefs).getBoolean("remove_pagination_component", false)
        val removeFolder =
            prefs(ModulePrefs).getBoolean("remove_folder_pagination_component", false)
        val disableSliding =
            prefs(ModulePrefs).getBoolean("disable_pagination_component_sliding", false)

        //Source OplusPageIndicator
        "com.android.launcher.pageindicators.OplusPageIndicator".toClass().apply {
            val drawMethod = hasMethod { name = "dispatchDraw" }
            method {
                name = if (drawMethod) "dispatchDraw" else "onDraw"
                param(CanvasClass)
            }.hook {
                before {
                    val view = instance<View>()
                    val parentView = if (view.parent != null) view.parent as View else return@before
                    val entryName = safeOfNull {
                        view.resources.getResourceEntryName(parentView.id)
                    } ?: return@before
                    when (entryName) {
                        "drag_layer" -> if (removeDesktop) {
                            view.isVisible = false
                            resultNull()
                        }

                        "folder_content_root" -> if (removeFolder) {
                            view.isVisible = false
                            resultNull()
                        }
                    }
                }
            }
        }

        if (SDK < A13) return

        //Source PageIndicatorTouchHelper
        "com.android.launcher.pageindicators.PageIndicatorTouchHelper".toClassOrNull()?.apply {
            method { name = "dispatchTouchEvent";param(MotionEventClass) }.hook {
                if (disableSliding) replaceToFalse()
            }
        }

        //Source BigFolderIcon
        "com.android.launcher3.folder.big.BigFolderIcon".toClassOrNull()?.apply {
            method { name = "onScrollPageStart" }.hook {
                after {
                    if (removeFolder) field { name = "indicator" }.get(instance)
                        .cast<View>()?.isVisible = false
                }
            }
            method { name = "exposureForWorkspace" }.hook {
                after {
                    if (removeFolder) field { name = "indicator" }.get(instance)
                        .cast<View>()?.isVisible = false
                }
            }
        }
    }
}