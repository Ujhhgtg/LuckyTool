package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object RemoveStatusBarBottomNetworkWarn : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) loadHooker(StatusBarBottomNetworkWarn)
        else loadHooker(StatusBarBottomNetworkWarnOld)
    }

    @Obfuscate
    object StatusBarBottomNetworkWarn : YukiBaseHooker() {
        override fun onHook() {
            var removeMode = prefs(ModulePrefs).getString("remove_control_center_networkwarn", "0")
            dataChannel.wait<String>("remove_control_center_networkwarn") { removeMode = it }

            //Source OplusQSSecurityController
            "com.oplus.systemui.qs.policy.OplusQSSecurityController".toClass().apply {
                val hasRefreshState = hasMethod { name = "handleRefreshState" }
                method { name = "showDeviceMonitoringDialog" }.hook {
                    if (removeMode == "1" || removeMode == "2") intercept()
                }
                method {
                    name {
                        if (hasRefreshState) it == "handleRefreshState"
                        else it.contains("handleRefreshState")
                    }
                }.hook {
                    if (removeMode == "2") intercept()
                }
            }
        }
    }

    @Obfuscate
    object StatusBarBottomNetworkWarnOld : YukiBaseHooker() {
        override fun onHook() {
            var removeMode = prefs(ModulePrefs).getString("remove_control_center_networkwarn", "0")
            dataChannel.wait<String>("remove_control_center_networkwarn") { removeMode = it }

            //Source OplusQSSecurityText
            VariousClass(
                "com.oplusos.systemui.qs.widget.OplusQSSecurityText", //C13
                "com.oplus.systemui.qs.widget.OplusQSSecurityText" //C14
            ).toClass().apply {
                method { name = "handleClick" }.hook {
                    if (removeMode == "1" || removeMode == "2") intercept()
                }
                method { name = "handleRefreshState" }.hook {
                    if (removeMode == "2") intercept()
                }
            }
        }
    }
}