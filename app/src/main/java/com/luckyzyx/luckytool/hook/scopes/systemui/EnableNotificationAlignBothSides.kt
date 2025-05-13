package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A15
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getScreenOrientation
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableNotificationAlignBothSides : YukiBaseHooker() {

    private var qsPanelPaddingPx = 0
    override fun onHook() {
        //Source ExpandableNotificationRow
        "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow".toClass()
            .apply {
                method { name = "onFinishInflate" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
                method { name = "onLayout" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
                method { name = "reInflateViews" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
                method { name = "onConfigurationChanged" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
                method { name = "onUiModeChanged" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
                method { name = "onNotificationUpdated" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "ExpandableNotificationRow", method.name
                        )
                    }
                }
            }

        if (SDK >= A13) loadHooker(OtherNotification) else loadHooker(OtherNotificationC12)
    }

    @Obfuscate
    private object OtherNotification : YukiBaseHooker() {
        override fun onHook() {
            //Source KeyguardMediaController -> MediaHost -> HostView -> parent
            VariousClass(
                "com.android.systemui.media.KeyguardMediaController", //C13
                "com.android.systemui.media.controls.ui.KeyguardMediaController", //C14
                "com.android.systemui.media.controls.ui.controller.KeyguardMediaController" //C15
            ).toClass().apply {
                method { name = "setVisibility";paramCount = 2 }.hook {
                    before {
                        if (SDK >= A15) return@before
                        val viewGroup = args().first().cast<ViewGroup>() ?: return@before
                        val visible = args().last().cast<Int>() ?: return@before
                        val count = viewGroup.childCount
                        if ((visible == 0) && (count > 0)) {
                            if (viewGroup.width != 0) viewGroup.setViewWidth(
                                "KeyguardMediaController", method.name
                            )
                        }
                    }
                }
            }

            //Source UbiquitousExpandableRow
            VariousClass(
                "com.oplusos.systemui.statusbar.notification.row.UbiquitousExpandableRow", //C13
                "com.oplus.systemui.statusbar.notification.row.UbiquitousExpandableRow" //C14 or null
            ).toClassOrNull()?.apply {
                method { name = "onFinishInflate" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "UbiquitousExpandableRow", method.name
                        )
                    }
                }
                method { name = "onLayout" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "UbiquitousExpandableRow", method.name
                        )
                    }
                }
                method { name = "reInflateViews" }.hook {
                    after {
                        instance<ViewGroup>().setViewWidth(
                            "UbiquitousExpandableRow", method.name
                        )
                    }
                }
            }

            //Source NotificationSeedingController C14
            "com.oplus.systemui.plugins.seedling.notification.NotificationSeedingController".toClassOrNull()
                ?.apply {
                    method { name = "onCreateView" }.hook {
                        after {
                            field { name = "parent" }.get(instance).cast<ViewGroup>()
                                ?.setViewWidth("NotificationSeedingController", method.name)
                        }
                    }
                    method { name = "onUpdate" }.hook {
                        after {
                            field { name = "parent" }.get(instance).cast<ViewGroup>()
                                ?.setViewWidth("NotificationSeedingController", method.name)
                        }
                    }
                    method { name = "refreshNotificationPosition" }.hook {
                        after {
                            field { name = "parent" }.get(instance).cast<ViewGroup>()
                                ?.setViewWidth("NotificationSeedingController", method.name)
                        }
                    }
                    method { name = "updateNotifSeedingViews" }.hook {
                        after {
                            field { name = "parent" }.get(instance).cast<ViewGroup>()
                                ?.setViewWidth("NotificationSeedingController", method.name)
                        }
                    }
                }

            //Source OplusCustomRow C15
            "com.oplus.systemui.statusbar.notification.customcard.OplusCustomRow".toClassOrNull()
                ?.apply {
                    method { name = "onFinishInflate" }.hook {
                        after { instance<ViewGroup>().setViewWidth("OplusCustomRow", method.name) }
                    }
                    method { name = "onLayout" }.hook {
                        after { instance<ViewGroup>().setViewWidth("OplusCustomRow", method.name) }
                    }
                    method { name = "onConfigurationChanged" }.hook {
                        after { instance<ViewGroup>().setViewWidth("OplusCustomRow", method.name) }
                    }
                }
        }
    }

    @Obfuscate
    private object OtherNotificationC12 : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusMediaHost
            "com.oplusos.systemui.media.OplusMediaHost".toClass().apply {
                method { name = "updateViewVisibility" }.hook {
                    before {
                        val hostView = field { name = "hostView";superClass() }.get(instance)
                            .cast<ViewGroup>() ?: return@before
                        val visible = hostView.visibility
                        val count = hostView.childCount
                        if ((visible == 0) && (count > 0)) {
                            if (hostView.width != 0) hostView.setViewWidth(
                                "OplusMediaHost", method.name
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun View.setViewWidth(cls: String, methodName: String) {
        qsPanelPaddingPx = resources.getDimensionPixelSize(
            resources.getIdentifier("qs_header_panel_side_padding", "dimen", packageName)
        )
        val targetWidth = resources.displayMetrics.widthPixels - (qsPanelPaddingPx * 2)

        getScreenOrientation(this) {
            if (layoutParams != null) when (layoutParams) {
                is FrameLayout.LayoutParams -> {
                    layoutParams = FrameLayout.LayoutParams(layoutParams).apply {
                        width = if (it) targetWidth else FrameLayout.LayoutParams.MATCH_PARENT
                    }
                }

                else -> {
                    layoutParams = ViewGroup.LayoutParams(layoutParams).apply {
                        width = if (it) targetWidth else ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
            }
        }
    }
}