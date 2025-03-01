package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

@Obfuscate
object BluetoothIconRelated : YukiBaseHooker() {

    private val BluetoothController = "com.android.systemui.statusbar.policy.BluetoothController"
    private val StatusBarIconController =
        "com.android.systemui.statusbar.phone.ui.StatusBarIconController"

    override fun onHook() {
        var isHide = prefs(ModulePrefs).getBoolean("hide_icon_when_bluetooth_not_connected", false)
        dataChannel.wait<Boolean>("hide_icon_when_bluetooth_not_connected") { isHide = it }

        //Source PhoneStatusBarPolicyEx
        VariousClass(
            "com.oplusos.systemui.statusbar.phone.PhoneStatusBarPolicyEx", //C13
            "com.oplus.systemui.statusbar.phone.OplusPhoneStatusBarPolicyExImpl" //C14
        ).toClass().apply {
            val hasUpdateBluetoothIcon = hasMethod { name = "updateBluetoothIcon" }
            val hasUpdateBluetooth = hasMethod { name = "updateBluetooth" }
            if (hasUpdateBluetoothIcon) {
                method { name = "updateBluetoothIcon";paramCount = 4 }.hook {
                    before {
                        if (!isHide) return@before
                        val isBluetoothEnabled = args().last().boolean()
                        val controller = field {
                            type = BluetoothController
                            superClass(SDK < A14)
                        }.get(instance).any() ?: return@before
                        val isBluetoothConnected = controller.current().method {
                            name = "isBluetoothConnected"
                        }.invoke<Boolean>() ?: return@before
                        args().last().set(isBluetoothEnabled && isBluetoothConnected)
                    }
                }
            } else {
                method {
                    name {
                        if (hasUpdateBluetooth) it == "updateBluetooth"
                        else it.contains("updateBluetooth")
                    }
                }.hook {
                    before {
                        if (!isHide) return@before
                        val bluetoothController = field {
                            type = BluetoothController
                            superClass(SDK < A14)
                        }.get(instance).any() ?: return@before
                        val statusBarIconController =
                            field { type = StatusBarIconController }.get(instance).any()
                                ?: return@before
                        val slotBluetooth = field { name = "slotBluetooth" }.get(instance).string()
                        val isBluetoothEnabled = bluetoothController.current().field {
                            name = "mEnabled"
                        }.boolean()
                        val bluetoothConnectionState = bluetoothController.current().field {
                            name = "mConnectionState"
                        }.int()
                        if (isBluetoothEnabled && bluetoothConnectionState != 2) {
                            statusBarIconController.current().method { name = "setIconVisibility" }
                                .call(slotBluetooth, false)
                            resultNull()
                        }
                    }
                }
            }
        }
    }
}