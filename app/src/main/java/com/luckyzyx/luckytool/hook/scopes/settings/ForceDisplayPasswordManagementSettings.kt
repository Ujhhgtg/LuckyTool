package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object ForceDisplayPasswordManagementSettings : YukiBaseHooker() {

    override fun onHook() {
        if (getOSVersionCode >= 30) loadHooker(PasswordManagementSettings)
        else loadHooker(PasswordManagementSettingsV13)
    }

    @Obfuscate
    object PasswordManagementSettings : YukiBaseHooker() {
        override fun onHook() {
            //Source PasswordManagerPreferenceController
            "com.oplus.settings.feature.password.controller.PasswordManagerPreferenceController".toClass()
                .apply {
                    method { name = "isPreferenceNotAvailable" }.hook {
                        replaceToFalse()
                    }
                }
        }
    }

    @Obfuscate
    object PasswordManagementSettingsV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source PasswordManagerPreferenceController
            "com.oplus.settings.feature.password.controller.PasswordManagerPreferenceController".toClass()
                .apply {
                    method { name = "displayPreference" }.hook {
                        after {
                            val preferenceScreen = args().first().any() ?: return@after
                            val preference = preferenceScreen.current().method {
                                name = "findPreference"
                                param(CharSequenceClass)
                                superClass()
                            }.call("key_password_manager") ?: return@after
                            preference.current().method {
                                name = "setVisible"
                                param(BooleanType)
                                superClass()
                            }.call(true)
                        }
                    }
                    method { name = "updateState" }.hook {
                        after {
                            val preference = args().first().any() ?: return@after
                            preference.current().method {
                                name = "setVisible"
                                param(BooleanType)
                                superClass()
                            }.call(true)
                        }
                    }
                }
        }
    }
}