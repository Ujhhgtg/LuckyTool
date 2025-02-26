package com.luckyzyx.luckytool.hook.scopes.systemui

import android.telephony.SubscriptionManager
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.FlowUtils
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object MobileDataIconRelated : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        when (osCode) {
            in 34..Int.MAX_VALUE -> loadHooker(MobileDataIcon)
            in 23..33 -> loadHooker(MobileDataIconV14)
            else -> loadHooker(MobileDataIconV120)
        }
    }

    @Obfuscate
    object MobileDataIcon : YukiBaseHooker() {
        override fun onHook() {
            //        val removeIcon = prefs(ModulePrefs).getBoolean("remove_mobile_data_icon", false)
            val removeInout = prefs(ModulePrefs).getBoolean("remove_mobile_data_inout", false)
            val removeType = prefs(ModulePrefs).getBoolean("remove_mobile_data_type", false)
            val hideNonNetwork = prefs(ModulePrefs).getBoolean("hide_non_network_card_icon", false)
            var hideNoSS = prefs(ModulePrefs).getBoolean("hide_nosim_noservice", false)
            dataChannel.wait<Boolean>("hide_nosim_noservice") { hideNoSS = it }

            //Source OplusStatusBarMobileViewBinder
            "com.oplus.systemui.statusbar.pipeline.mobile.ui.view.OplusStatusBarMobileViewBinder".toClass()
                .apply {
                    val hasDataActivity = hasMethod { name = "bindCustEx\$updateDataActivity" }
                    val hasNetworkType = hasMethod { name = "bindCustEx\$updateNetworkType" }
                    if (hasDataActivity) method { name = "bindCustEx\$updateDataActivity" }.hook {
                        before {
                            if (removeInout) args().last().set(0)
                        }
                    }
                    if (hasNetworkType) method { name = "bindCustEx\$updateNetworkType" }.hook {
                        before {
                            if (removeType) args().last().setNull()
                        }
                    }
                }

            //Source OplusMobileIconViewModel
            "com.oplus.systemui.statusbar.pipeline.mobile.ui.viewmodel.OplusMobileIconViewModel".toClass()
                .apply {
                    method {
                        name = "isVisible"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        before {
                            if (!hideNonNetwork) return@before
                            val field = field { name = "isVisible" }.get(instance).any()
                            if (field != null) {
                                val value = field.current().method {
                                    name = "getValue";superClass()
                                }.boolean()
                                if (!value) return@before
                            }
                            val subId = field { name = "subscriptionId" }.get(instance).int()
                            val localSubId = SubscriptionManager.getDefaultDataSubscriptionId()
                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(subId == localSubId)
                                    ?: return@before
                                it.asStateFlow(mutableStateFlow) ?: return@before
                            }
                        }
                    }
                }

            //Source MobileIconViewModel
            "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClass()
                .apply {
                    method {
                        name = "isVisible"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        before {
                            if (!hideNonNetwork) return@before
                            val field = field { name = "isVisible" }.get(instance).any()
                            if (field != null) {
                                val value = field.current().method {
                                    name = "getValue";superClass()
                                }.boolean()
                                if (!value) return@before
                            }
                            val subId = field { name = "subscriptionId" }.get(instance).int()
                            val localSubId = SubscriptionManager.getDefaultDataSubscriptionId()
                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(subId == localSubId)
                                    ?: return@before
                                it.asStateFlow(mutableStateFlow) ?: return@before
                            }
                        }
                    }
                }

            //Source LocationBasedMobileViewModel
            "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.LocationBasedMobileViewModel".toClass()
                .apply {
                    method {
                        name = "isVisible"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        before {
                            if (!hideNonNetwork) return@before
                            val subId = field { name = "subscriptionId" }.get(instance).int()
                            val localSubId = SubscriptionManager.getDefaultDataSubscriptionId()
                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(subId == localSubId)
                                    ?: return@before
                                it.asStateFlow(mutableStateFlow) ?: return@before
                            }
                        }
                    }
                }

            //Source OplusStatusBarSignalPolicy
            "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarSignalPolicy".toClass().apply {
                val hasSlotIconVisibility = hasMethod { name = "updateSlotIconVisibility" }
                method {
                    name {
                        if (hasSlotIconVisibility) it == "updateSlotIconVisibility"
                        else it.contains("updateSlotIconVisibility")
                    }
                    paramCount(3..4)
                }.hook {
                    before {
                        if (!hideNoSS) return@before
                        val keyIndex = if (hasSlotIconVisibility) 0 else 1
                        val valueIndex = if (hasSlotIconVisibility) 1 else 2
                        val key = args(keyIndex).string()
                        if (key == "nosim_all") args(valueIndex).set(0)
                    }
                }
            }
        }
    }

    @Obfuscate
    object MobileDataIconV14 : YukiBaseHooker() {
        override fun onHook() {
            //        val removeIcon = prefs(ModulePrefs).getBoolean("remove_mobile_data_icon", false)
            val removeInout = prefs(ModulePrefs).getBoolean("remove_mobile_data_inout", false)
            val removeType = prefs(ModulePrefs).getBoolean("remove_mobile_data_type", false)
            var hideNonNetwork = prefs(ModulePrefs).getBoolean("hide_non_network_card_icon", false)
            dataChannel.wait<Boolean>("hide_non_network_card_icon") { hideNonNetwork = it }
            var hideNoSS = prefs(ModulePrefs).getBoolean("hide_nosim_noservice", false)
            dataChannel.wait<Boolean>("hide_nosim_noservice") { hideNoSS = it }

            //Source OplusStatusBarMobileViewExImpl -> initView
            VariousClass(
                "com.oplusos.systemui.statusbar.OplusStatusBarMobileView", //C12.1
                "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarMobileViewExImpl" //C13
            ).toClass().apply {
                method { name = "initViewState" }.hook {
                    after {
                        if (hideNonNetwork) {
                            val state = args().first().any()
                            val subId = state?.current()?.field { name = "subId" }?.int()
                            val subId2 = SubscriptionManager.getDefaultDataSubscriptionId()
                            field { name = "mMobileGroup" }.get(instance)
                                .cast<ViewGroup>()?.isVisible = subId == subId2
                        }
//                    if (removeIcon) field { name = "mMobileGroup" }.get(instance)
//                        .cast<ViewGroup>()?.isVisible = false
                        if (removeInout) field { name = "mDataActivity" }.get(instance)
                            .cast<View>()?.isVisible = false
                        if (removeType) field {
                            name = "mMobileType"
                            if (SDK < A13) superClass(true)
                        }.get(instance).cast<View>()?.isVisible = false
                    }
                }
                method {
                    name = when (simpleName) {
                        "OplusStatusBarMobileView" -> "updateMobileViewState"
                        "OplusStatusBarMobileViewExImpl" -> "updateState"
                        else -> "updateState"
                    }
                }.hook {
                    after {
                        if (hideNonNetwork) {
                            val state = args().first().any()
                            val subId = state?.current()?.field { name = "subId" }?.int()
                            val subId2 = SubscriptionManager.getDefaultDataSubscriptionId()
                            field { name = "mMobileGroup" }.get(instance)
                                .cast<ViewGroup>()?.isVisible = subId == subId2
                        }
//                    if (removeIcon) field { name = "mMobileGroup" }.get(instance)
//                        .cast<ViewGroup>()?.isVisible = false
                        if (removeInout) field { name = "mDataActivity" }.get(instance)
                            .cast<View>()?.isVisible = false
                        if (removeType) field {
                            name = "mMobileType"
                            if (SDK < A13) superClass(true)
                        }.get(instance).cast<View>()?.isVisible = false
                    }
                }
            }

            //Source OplusStatusBarSignalPolicyExImpl
            VariousClass(
                "com.oplusos.systemui.ext.StatusBarSignalPolicyExt", //C12.1
                "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarSignalPolicyExImpl" //C13
            ).toClass().apply {
                val hasController = hasMethod { name = "getIconController" }
                method {
                    name = "setNoSims"
                    paramCount = 3
                }.hook {
                    after {
                        if (!hideNoSS) return@after
                        val iconController = if (hasController) method {
                            name = "getIconController"
                        }.get(instance).call()
                        else field { name = "iconController" }.get(instance).any()
                        val slotNoSim = field { name = "slotNoSim" }.get(instance).cast<String>()
                        iconController?.current()?.method {
                            name = "setIconVisibility"
                            paramCount = 2
                            if (simpleName == "StatusBarSignalPolicyExt") superClass()
                        }?.call(slotNoSim, false)
                    }
                }
            }
        }
    }

    @Obfuscate
    object MobileDataIconV120 : YukiBaseHooker() {
        override fun onHook() {
//        val removeIcon = prefs(ModulePrefs).getBoolean("remove_mobile_data_icon", false)
            val removeInout = prefs(ModulePrefs).getBoolean("remove_mobile_data_inout", false)
            val removeType = prefs(ModulePrefs).getBoolean("remove_mobile_data_type", false)
            var hideNonNetwork = prefs(ModulePrefs).getBoolean("hide_non_network_card_icon", false)
            dataChannel.wait<Boolean>("hide_non_network_card_icon") { hideNonNetwork = it }
            var hideNoSS = prefs(ModulePrefs).getBoolean("hide_nosim_noservice", false)
            dataChannel.wait<Boolean>("hide_nosim_noservice") { hideNoSS = it }

            //Source StatusBarMobileView
            "com.android.systemui.statusbar.StatusBarMobileView".toClass().apply {
                method { name = "initViewState" }.hook {
                    after {
                        if (hideNonNetwork) {
                            val state = field { name = "mState" }.get(instance).any()
                            val subId = state?.current()?.field { name = "subId" }?.int()
                            val subId2 = SubscriptionManager.getDefaultDataSubscriptionId()
                            field { name = "mMobileGroup" }.get(instance)
                                .cast<ViewGroup>()?.isVisible = subId == subId2
                        }
//                    if (removeIcon) field { name = "mMobileGroup" }.get(instance)
//                        .cast<ViewGroup>()?.isVisible = false
                        if (removeInout) field { name = "mInoutContainer" }.get(instance)
                            .cast<View>()?.isVisible = false
                        if (removeType) field { name = "mMobileType" }.get(instance)
                            .cast<View>()?.isVisible = false
                    }
                }
                method { name = "updateState" }.hook {
                    after {
                        if (hideNonNetwork) {
                            val state = field { name = "mState" }.get(instance).any()
                            val subId = state?.current()?.field { name = "subId" }?.int()
                            val subId2 = SubscriptionManager.getDefaultDataSubscriptionId()
                            field { name = "mMobileGroup" }.get(instance)
                                .cast<ViewGroup>()?.isVisible = subId == subId2
                        }
//                    if (removeIcon) field { name = "mMobileGroup" }.get(instance)
//                        .cast<ViewGroup>()?.isVisible = false
                        if (removeInout) field { name = "mInoutContainer" }.get(instance)
                            .cast<View>()?.isVisible = false
                        if (removeType) field { name = "mMobileType" }.get(instance)
                            .cast<View>()?.isVisible = false
                    }
                }
            }

            //Source SignalClusterView
            "com.oplusos.systemui.statusbar.widget.SignalClusterView".toClass().apply {
                method { name = "updateNoSimView" }.hook {
                    after {
                        if (!hideNoSS) return@after
                        val mNoSims = field { name = "mNoSims" }.get(instance).cast<View>()
                        method { name = "animateHide" }.get(instance).call(mNoSims, 8)
                    }
                }
            }
        }
    }
}

