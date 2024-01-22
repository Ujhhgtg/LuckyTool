package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.drawable.ShapeDrawable
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.ThemeUtils.isNightMode
import com.luckyzyx.luckytool.utils.setColorAlpha

object CustomBackGroundTransparencyForInActiveTiles : YukiBaseHooker() {
    override fun onHook() {
        val customAlpha =
            prefs(ModulePrefs).getInt("custom_background_transparency_for_inactive_tiles", -1)

        //Source OplusQsMediaPanelBgDrawable
        "com.oplus.systemui.qs.media.OplusQsMediaPanelBgDrawable".toClass().apply {
            constructor { paramCount = 5 }.hook {
                before {
                    if (customAlpha < 0) return@before
                    val value = customAlpha / 10.0F
                    val view = args().first().cast<View>() ?: return@before
                    if (view.context.isNightMode) return@before
                    val color = args(1).int()
                    val newColor = setColorAlpha(color, value)
                    args(1).set(newColor)
                }
            }
        }

        //Source OplusQSTileBaseView status_bar_qs_tile_bg_color_inactive
        "com.oplus.systemui.qs.qstileimpl.OplusQSTileBaseView".toClass().apply {
            method { name = "generateDrawable" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val value = customAlpha / 10.0F
                    val view = instance<View>()
                    if (view.context.isNightMode) return@after
                    val type = args().first().int()
                    val shapeDrawable = result<ShapeDrawable>() ?: return@after
                    if (type == 1) {
                        val color = shapeDrawable.paint.color
                        val newColor = setColorAlpha(color, value)
                        shapeDrawable.paint.color = newColor
                    }
                }
            }
        }

        //Source OplusQSHighlightTileView status_bar_qs_tile_bg_color_inactive
        "com.oplus.systemui.qs.qstileimpl.OplusQSHighlightTileView".toClass().apply {
            method { name = "generateDrawable" }.hook {
                after {
                    if (customAlpha < 0) return@after
                    val value = customAlpha / 10.0F
                    val view = instance<View>()
                    if (view.context.isNightMode) return@after
                    val type = args().first().int()
                    val shapeDrawable = result<ShapeDrawable>() ?: return@after
                    if (type == 1) {
                        val color = shapeDrawable.paint.color
                        val newColor = setColorAlpha(color, value)
                        shapeDrawable.paint.color = newColor
                    }
                }
            }
        }
    }
}