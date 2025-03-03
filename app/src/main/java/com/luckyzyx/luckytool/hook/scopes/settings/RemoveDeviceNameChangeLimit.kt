package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveDeviceNameChangeLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source PhoneNameSettingsActivity -> AlertDialog

        //Source PhoneNameVerifyUtil
        "com.oplus.settings.feature.deviceinfo.aboutphone.PhoneNameVerifyUtil".toClass().apply {
            method { name = "activeVerifyPhoneName" }.hook {
                before {
                    val callback = args().last().any() ?: return@before
                    callback.current().method { name = "onSuccess" }.call(null)
                    resultNull()
                }
            }
            method { name = "timeScheduleVerifyPhoneName" }.hook {
                intercept()
            }
        }

        //Source WirelessDeviceVerifyUtils
        "com.oplus.settings.utils.WirelessDeviceVerifyUtils".toClass().apply {
            method { name = "activeVerifyPhoneName" }.hook {
                before {
                    val callback = args().last().any() ?: return@before
                    callback.current().method { name = "onSuccess" }.call(null)
                    resultNull()
                }
            }
        }

        //Source OplusDeviceInfoUtils
        "com.oplus.settings.utils.OplusDeviceInfoUtils".toClass().apply {
            method { name = "getVerifyNameCondition" }.hook {
                replaceToFalse()
            }
        }
    }
}