package com.luckyzyx.luckytool.hook.scope.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.luckypray.dexkit.DexKitBridge

class HookSettingsFeature(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookAppFeatureProvider(dexKitBridge))
        loadHooker(HookExpUst(dexKitBridge))
        if (SDK >= A13) loadHooker(HookCustomizeFeature(dexKitBridge))
    }

    class HookExpUst(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            val neverTimeout = prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)

            //Source ExpUstUtils
            dexKitBridge.findClass {
                matcher {
                    methods {
                        add { returnType(StringClass) }
                        add { returnType(BooleanType) }
                        add { returnType(ApplicationInfoClass) }
                        add { paramTypes(StringClass) }
                        add { paramTypes(IntType) }
                        add { paramTypes(IntType, StringClass) }
                        add { paramTypes(StringClass) }
                        add { paramTypes(StringClass, StringClass) }
                    }
                    usingStrings("screen_off_timeout")
                }
            }.apply {
                checkDataList("HookExpUst")
                first().name.toClass().apply {
                    method { param(IntType);returnType = BooleanType }.hookAll {
                        before {
                            when (args().first().int()) {
                                //Source DisplayTimeOutController -> 永不息屏(24H)
                                11 -> if (SDK < A13 && neverTimeout) resultTrue()
                            }
                        }
                    }
                }
            }
        }
    }

    private class HookAppFeatureProvider(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            val isDisableCN =
                prefs(ModulePrefs).getBoolean("disable_cn_special_edition_setting", false)
            val neverTimeout = prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)
            val processorDetail = prefs(ModulePrefs).getString("set_processor_click_page", "0")
            val processManagement =
                prefs(ModulePrefs).getBoolean("force_display_process_management", false)

            //Source AppFeatureProviderUtils
            dexKitBridge.findClass {
                matcher {
                    methods {
                        add {
                            paramTypes(
                                ContentResolverClass, StringClass, BooleanType
                            )
                            returnType(BooleanType)
                        }
                        add {
                            paramTypes(
                                ContentResolverClass, StringClass, IntType
                            )
                            returnType(IntType)
                        }
                        add {
                            paramTypes(
                                ContentResolverClass, StringClass, StringClass
                            )
                            returnType(StringClass)
                        }
                        add {
                            paramTypes(ContentResolverClass, StringClass)
                            returnType(ListClass)
                        }
                        add {
                            paramTypes(ContentResolverClass, StringClass)
                            returnType(BooleanType)
                        }
                    }
                    usingStrings("AppFeatureProviderUtils")
                }
            }.apply {
                checkDataList("HookAppFeatureProvider")
                first().name.toClass().apply {
                    method {
                        param(ContentResolverClass, StringClass)
                        returnType = BooleanType
                    }.hook {
                        before {
                            when (args().last().string()) {
                                //Source OplusDefaultAutofillPicker -> autofill_password 自动填充密码
                                "com.android.settings.cn_version" -> if (isDisableCN) resultFalse()
                                //Source DisplayTimeOutController -> 永不息屏(24H)
                                "com.android.settings.show_never_timeout" -> if (neverTimeout) resultTrue()
                                //com.android.settings.processor_detail
                                "com.android.settings.processor_detail" -> if (processorDetail != "0") resultTrue()
                                //com.android.settings.processor_detail_gen2
                                "com.android.settings.processor_detail_gen2" -> if (processorDetail == "2") resultTrue()
                                //com.android.settings.ultimate_cleanup
                                "com.android.settings.ultimate_cleanup" -> if (processManagement) resultTrue()
                            }
                        }
                    }
                }
            }
        }
    }

    private class HookCustomizeFeature(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            val screenSizeCM = prefs(ModulePrefs).getBoolean("screen_physics_size_shown_cm", false)

            //Source CustomizeFeatureUtils
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(BooleanClass.name)
                        addForType(ListClass.name)
                    }
                    methods {
                        add { paramCount(0);returnType(BooleanType) }
                        add { paramCount(0);returnType(StringClass) }
                        add { paramCount(0);returnType(ListClass) }
                        add { paramCount(0);returnType(ContentResolverClass) }
                        add {
                            paramTypes(StringClass)
                            returnType(BooleanType)
                        }
                        add {
                            paramTypes("android.os.PersistableBundle")
                            returnType(BooleanType)
                        }
                        add {
                            paramTypes(StringClass)
                            returnType(ListClass)
                        }
                    }
                    usingStrings("CustomizeFeatureUtils")
                }
            }.apply {
                checkDataList("HookCustomizeFeature")
                first().name.toClass().apply {
                    method { param(StringClass);returnType = BooleanType }.hookAll {
                        before {
                            when (args().first().string()) {
                                //Source DeviceInfoUtils 屏幕尺寸显示厘米
                                "com.android.settings.screen_physics_size_cm" -> if (screenSizeCM) resultTrue()
                            }
                        }
                    }
                }
            }
        }
    }
}