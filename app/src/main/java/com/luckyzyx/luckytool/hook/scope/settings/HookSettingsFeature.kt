package com.luckyzyx.luckytool.hook.scope.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.hook.hooker.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookSettingsFeature : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        loadHooker(HookSysFeature)
        loadHooker(HookAppFeatureProvider)
        loadHooker(HookExpUst)
        if (SDK >= A13) loadHooker(HookCustomizeFeature)
    }

    private object HookSysFeature : YukiBaseHooker() {
        override fun onHook() {
            val memcVideo = false
            //prefs(ModulePrefs).getBoolean("force_display_video_memc_frame_insertion", false)

            //Source SysFeatureUtils
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findClass {
                    matcher {
                        fields {
                            addForType(BooleanClass.name)
                        }
                        methods {
                            add { paramCount(0);returnType(BooleanType) }
                            add { paramTypes(ContextClass.name);returnType(BooleanType) }
                            add { paramTypes(StringClass.name);returnType(BooleanType) }
                        }
                        usingStrings("SysFeatureUtils")
                    }
                }.apply {
                    checkDataList("HookSysFeature")
                    first().name.toClass().apply {
                        method { param(StringClass);returnType = BooleanType }.hookAll {
                            before {
                                when (args().first().string()) {
                                    //Source Iris5SettingsFragment -> iris5_motion_fluency_optimization_switch
                                    "oplus.software.video.rm_memc" -> if (memcVideo) resultFalse()
                                    "oplus.software.display.pixelworks_enable" -> if (memcVideo) resultTrue()
                                    //Source OplusPwmDevelopController -> oplus.software.display.pwm_switch.support
                                    //"oplus.software.display.pwm_switch.support" -> resultTrue()
                                }
                            }
                        }
//                        ro.oplus.audio.support.meta_audio
//                        method { name = "isHoloAudioSupported" }.hook {
//                            replaceToTrue()
//                        }
                    }
                }
            }
        }
    }

    private object HookExpUst : YukiBaseHooker() {
        override fun onHook() {
            val neverTimeout = prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)

            //Source ExpUstUtils
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findClass {
                    matcher {
                        methods {
                            add { returnType(StringClass) }
                            add { returnType(BooleanType) }
                            add { returnType(ApplicationInfoClass) }
                            add { paramTypes(StringClass.name) }
                            add { paramTypes(IntType.name) }
                            add { paramTypes(IntType.name, StringClass.name) }
                            add { paramTypes(StringClass.name) }
                            add { paramTypes(StringClass.name, StringClass.name) }
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
    }

    private object HookAppFeatureProvider : YukiBaseHooker() {
        override fun onHook() {
            val isDisableCN =
                prefs(ModulePrefs).getBoolean("disable_cn_special_edition_setting", false)
            val neverTimeout = prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)
            val processorDetail = prefs(ModulePrefs).getString("set_processor_click_page", "0")
            val processManagement =
                prefs(ModulePrefs).getBoolean("force_display_process_management", false)

            //Source AppFeatureProviderUtils
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findClass {
                    matcher {
                        methods {
                            add {
                                paramTypes(
                                    ContentResolverClass.name, StringClass.name, BooleanType.name
                                )
                                returnType(BooleanType)
                            }
                            add {
                                paramTypes(
                                    ContentResolverClass.name, StringClass.name, IntType.name
                                )
                                returnType(IntType)
                            }
                            add {
                                paramTypes(
                                    ContentResolverClass.name, StringClass.name, StringClass.name
                                )
                                returnType(StringClass)
                            }
                            add {
                                paramTypes(ContentResolverClass.name, StringClass.name)
                                returnType(ListClass)
                            }
                            add {
                                paramTypes(ContentResolverClass.name, StringClass.name)
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
    }

    private object HookCustomizeFeature : YukiBaseHooker() {
        override fun onHook() {
            val screenSizeCM = prefs(ModulePrefs).getBoolean("screen_physics_size_shown_cm", false)

            //Source CustomizeFeatureUtils
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
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
                                paramTypes(StringClass.name)
                                returnType(BooleanType)
                            }
                            add {
                                paramTypes("android.os.PersistableBundle")
                                returnType(BooleanType)
                            }
                            add {
                                paramTypes(StringClass.name)
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
}