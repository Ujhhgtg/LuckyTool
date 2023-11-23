package com.luckyzyx.luckytool.hook.scope.systemui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.closeScreen
import com.luckyzyx.luckytool.utils.safeOfNull

object LockScreenBottomButton : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A14) loadHooker(LockScreenBottomButton)
        else loadHooker(LockScreenBottomButtonC13)
    }

    object LockScreenBottomButton : YukiBaseHooker() {
        override fun onHook() {
            var rmLeft =
                prefs(ModulePrefs).getBoolean("remove_lock_screen_bottom_left_button", false)
            dataChannel.wait<Boolean>("remove_lock_screen_bottom_left_button") { rmLeft = it }
            var rmRight =
                prefs(ModulePrefs).getBoolean("remove_lock_screen_bottom_right_camera", false)
            dataChannel.wait<Boolean>("remove_lock_screen_bottom_right_camera") { rmRight = it }
            var autoCloseScreen = prefs(ModulePrefs).getBoolean(
                "lock_screen_switch_flashlight_auto_close_screen", false
            )
            dataChannel.wait<Boolean>("lock_screen_switch_flashlight_auto_close_screen") {
                autoCloseScreen = it
            }

            //Source KeyguardBottomAreaViewBinder
            "com.android.systemui.keyguard.ui.binder.KeyguardBottomAreaViewBinder".toClass().apply {
                method { name = "updateButton" }.hook {
                    before {
                        val viewModel = args(1).any() ?: return@before
                        when (viewModel.current().field { name = "slotId" }.string()) {
                            "bottom_start" -> if (rmLeft) {
                                viewModel.current().method { name = "setVisible" }.call(false)
                            }

                            "bottom_end" -> if (rmRight) {
                                viewModel.current().method { name = "setVisible" }.call(false)
                            }
                        }
                    }
                }
            }

            //Source OplusFlashlightQuickAffordanceConfig
            "com.oplus.systemui.keyguard.data.quickaffordance.OplusFlashlightQuickAffordanceConfig".toClass()
                .apply {
                    method { name = "onTriggered" }.hook {
                        after {
                            if (rmLeft || !autoCloseScreen) return@after
                            val context = field { name = "context";superClass() }.get(instance)
                                .cast<Context>() ?: return@after
                            closeScreen(context)
                        }
                    }
                }
        }
    }

    object LockScreenBottomButtonC13 : YukiBaseHooker() {
        override fun onHook() {
            //affordance_magazine
            var rmLeft =
                prefs(ModulePrefs).getBoolean("remove_lock_screen_bottom_left_button", false)
            dataChannel.wait<Boolean>("remove_lock_screen_bottom_left_button") { rmLeft = it }
            //affordance_camera
            var rmRight =
                prefs(ModulePrefs).getBoolean("remove_lock_screen_bottom_right_camera", false)
            dataChannel.wait<Boolean>("remove_lock_screen_bottom_right_camera") { rmRight = it }

            //affordance_flashlight
            var useFlashLight = prefs(ModulePrefs).getBoolean(
                "lock_screen_bottom_left_button_replace_with_flashlight", false
            )
            dataChannel.wait<Boolean>("lock_screen_bottom_left_button_replace_with_flashlight") {
                useFlashLight = it
            }
            var autoCloseScreen = prefs(ModulePrefs).getBoolean(
                "lock_screen_switch_flashlight_auto_close_screen", false
            )
            dataChannel.wait<Boolean>("lock_screen_switch_flashlight_auto_close_screen") {
                autoCloseScreen = it
            }

            //Source KeyguardBottomAreaView
            "com.android.systemui.statusbar.phone.KeyguardBottomAreaView".toClass().apply {
                method { name = "onFinishInflate" }.hook {
                    before {
                        if (!useFlashLight) return@before
                        instance<ViewGroup>().context.injectModuleAppResources()
                    }
                }
                method { name = "updateLeftAffordanceIcon" }.hook {
                    after {
                        if (!useFlashLight) return@after
                        val context = instance<ViewGroup>().context
                        method { name = "updateLeftAffordanceVisibility" }.get(instance).call()
                        val mFlashlightController = field { name = "mFlashlightController" }
                            .get(instance).any()
                        val isEnable = mFlashlightController?.getIsEnable() ?: false
                        val resId = if (isEnable) R.drawable.affordance_flashlight_on
                        else R.drawable.affordance_flashlight
                        val drawable = safeOfNull {
                            ResourcesCompat.getDrawable(context.resources, resId, null)
                        }
                        field { name = "mLeftAffordanceView";superClass() }.get(instance).any()
                            ?.current()?.method {
                                name = "setImageDrawable";param(DrawableClass, BooleanType)
                                superClass()
                            }?.call(drawable, !isEnable)
                    }
                }
                method { name = "updateLeftAffordanceVisibility" }.hook {
                    after {
                        if (rmLeft) {
                            field { name = "mLeftAffordanceView";superClass() }.get(instance)
                                .cast<View>()?.isVisible = false
                            return@after
                        }
                        if (useFlashLight) {
                            field { name = "mLeftAffordanceView";superClass() }.get(instance)
                                .cast<ImageView>()?.isVisible = true
                        }
                    }
                }
                method { name = "launchLeftAffordance" }.hook {
                    before {
                        if (!useFlashLight) return@before
                        method { name = "baseLaunchLeftAffordance";superClass() }.get(instance)
                            .call()
                        val mFlashlightController =
                            field { name = "mFlashlightController" }.get(instance).any()
                        val isEnable = mFlashlightController?.getIsEnable() ?: true
                        mFlashlightController?.setFlashlight(!isEnable)
                        method { name = "updateLeftAffordanceIcon" }.get(instance).call()
                        if (autoCloseScreen) closeScreen(instance<ViewGroup>().context)
                        resultNull()
                    }
                }
                method { name = "updateCameraVisibility" }.hook {
                    before {
                        if (!rmRight) return@before
                        field { name = "mRightAffordanceView";superClass() }.get(instance)
                            .cast<ImageView>()?.isVisible = false
                        resultNull()
                    }
                }
            }
        }
    }

    private fun Any.getIsEnable(): Boolean? {
        return this.current().method { name = "isEnabled" }.invoke<Boolean>()
    }

    private fun Any.setFlashlight(status: Boolean) {
        this.current().method { name = "setFlashlight" }.call(status)
    }
}