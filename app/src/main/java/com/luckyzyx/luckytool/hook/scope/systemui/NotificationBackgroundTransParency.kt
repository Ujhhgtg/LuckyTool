package com.luckyzyx.luckytool.hook.scope.systemui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.utils.BackgroundBlurDrawableUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp

object NotificationBackgroundTransParency : YukiBaseHooker() {
    private var disableBlur = false

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        var customAlpha =
            prefs(ModulePrefs).getInt("custom_notification_background_transparency", -1)
        dataChannel.wait<Int>("custom_notification_background_transparency") {
            customAlpha = it
        }
        if (customAlpha < 0) return

        //Source NotificationBackgroundViewExtImp
        "com.oplus.systemui.statusbar.notification.row.NotificationBackgroundViewExtImp".toClass()
            .apply {
                method { name = "getOplusStyle";superClass() }.hook {
                    replaceToTrue()
                }
                method { name = "drawBlur";superClass() }.hook {
                    replaceToTrue()
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
                        BackgroundBlurDrawableUtils(appClassLoader).apply {
                            if (res.javaClass extends clazz) {
                                res.setBlurRadius(value.dp)
                                res.alpha = value
                            }
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
                method { name = "updateBackground";superClass() }.hook {
                    before {
                        if (customAlpha > 0) field { name = "mShowNoBackground" }.get(instance)
                            .setFalse()
                    }
                }
            }
    }
}