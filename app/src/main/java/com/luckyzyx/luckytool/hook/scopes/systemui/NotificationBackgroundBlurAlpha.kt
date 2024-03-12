package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.android.internal.graphics.drawable.BackgroundBlurDrawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp

object NotificationBackgroundBlurAlpha : YukiBaseHooker() {
    private var disableBlur = false

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        var customAlpha =
            prefs(ModulePrefs).getInt("custom_notification_background_transparency", -1)
        dataChannel.wait<Int>("custom_notification_background_transparency") {
            customAlpha = it
        }
        var enableBlur =
            prefs(ModulePrefs).getBoolean("enable_notification_background_blur_effect", false)
        dataChannel.wait<Boolean>("enable_notification_background_blur_effect") {
            enableBlur = it
        }

        //Source NotificationBackgroundView
        "com.android.systemui.statusbar.notification.row.NotificationBackgroundView".toClass()
            .apply {
                method { name = "draw";paramCount = 2 }.hook {
                    before {
                        if (customAlpha < 0 || enableBlur) return@before
                        val alphaValue = customAlpha * 25
                        val mBackground = args().last().cast<Drawable>() ?: return@before
                        mBackground.alpha = alphaValue
                    }
                }
            }

        //Source NotificationBackgroundViewExtImp
        "com.oplus.systemui.statusbar.notification.row.NotificationBackgroundViewExtImp".toClass()
            .apply {
                method { name = "getOplusStyle";superClass() }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        if (enableBlur) resultTrue() else resultFalse()
                    }
                }
                method { name = "drawBlur";superClass() }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        if (enableBlur) resultTrue() else resultFalse()
                    }
                }
                method { name = "decideBlurDrawable" }.hook {
                    before {
                        if (customAlpha < 0) return@before
                        if (!disableBlur) method { name = "getRowBlurDelegate";superClass() }
                            .get(instance).call()?.current()?.method {
                                name = "setBlurType";superClass()
                            }?.call(1)
                    }
                    after {
                        if (customAlpha < 0) return@after
                        val value = customAlpha * 25
                        val res = result<Drawable>() ?: return@after
                        if (res is BackgroundBlurDrawable) {
                            val mBlurRadius = res.current().field { name = "mBlurRadius" }.int()
                            if (mBlurRadius != value.dp) res.setBlurRadius(value.dp)
                        }
                    }
                }
            }

        //Source OplusRowsBlurManager
        "com.oplus.systemui.blur.OplusRowsBlurManager".toClass().apply {
            method { name = "blurMediaPanel" }.hook {
                before {
                    if (customAlpha < 0) return@before
                    disableBlur = args().first().boolean()
                }
            }
        }

        //Source ExpandableNotificationRow
        "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow".toClass()
            .apply {
                method { name = "updateBackgroundForGroupState" }.hook {
                    before {
                        if (customAlpha < 0 || !enableBlur) return@before
                        field { name = "mShowGroupBackgroundWhenExpanded" }.get(instance).setTrue()
                    }
                }
            }

        //Source SeedlingItemRow
        "com.oplus.systemui.plugins.seedling.notification.widget.SeedlingItemRow".toClass().apply {
            method { name = "initBackground" }.hook {
                after {
                    val view = instance<View>()
                    val drawableId = view.resources.getIdentifier(
                        "notification_seed_action_rounded_bg", "drawable",
                        this@NotificationBackgroundBlurAlpha.packageName
                    )
                    val drawable = AppCompatResources.getDrawable(view.context, drawableId)
                        ?: return@after
                    val newDrawable = drawable.mutate().apply {
                        alpha = 255 / 10 * customAlpha
                    }
                    val backgroundNormal = field { name = "mBackgroundNormal" }.get(instance).any()
                        ?: return@after
                    backgroundNormal.current().method {
                        name = "setCustomBackground";param(DrawableClass)
                    }.call(newDrawable)
                    backgroundNormal.current().method {
                        name = "setTint";param(IntType)
                    }.call(0)
                }
            }
        }
    }
}