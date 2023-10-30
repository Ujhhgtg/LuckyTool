package com.luckyzyx.luckytool.hook.scope.systemui

import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.utils.BackgroundBlurDrawableUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp

object NotificationBackgroundTransParency : YukiBaseHooker() {
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
                method { name = "decideBlurDrawable" }.hook {
                    after {
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

        //Source RowBlurDelegate
        "com.oplus.systemui.blur.RowBlurDelegate".toClass().apply {
            method { name = "getBlurType" }.hook {
                replaceTo(1)
            }
            method { name = "setBlurType" }.hook {
                before {
                    args().first().set(1)
                }
            }
            method { name = "getBlurViewType" }.hook {
                replaceTo(2)
            }
            method { name = "setBlurViewType" }.hook {
                before {
                    args().first().set(2)
                }
            }
            method { name = "isKgOccluded" }.hook {
                replaceToFalse()
            }
            method { name = "getTempForbidBlur" }.hook {
                replaceToFalse()
            }
        }
    }
}