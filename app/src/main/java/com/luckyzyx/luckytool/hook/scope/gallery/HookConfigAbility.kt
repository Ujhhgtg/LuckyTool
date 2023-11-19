package com.luckyzyx.luckytool.hook.scope.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

class HookConfigAbility(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        //启用水印编辑
        val waterMark = prefs(ModulePrefs).getBoolean("enable_watermark_editing", false)
        //替换OnePlus机型水印
        val notOplus = prefs(ModulePrefs).getBoolean("replace_oneplus_model_watermark", false)
        //启用闪速抠图
        val lnsImage = prefs(ModulePrefs).getBoolean("enable_lns_cut_photo", false)

        //Source ConfigAbilityImpl
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass.name)
                }
                methods {
                    add { name = "close";paramCount(0) }
                    add { name = "contains";paramTypes(StringClass) }
                    add { returnType(AutoCloseable::class.java) }
                    add {
                        paramTypes(StringClass, IntType)
                        returnType(IntClass)
                    }
                    add {
                        paramTypes(StringClass, LongType)
                        returnType(LongClass)
                    }
                    add {
                        paramTypes(StringClass, StringClass)
                        returnType(StringClass)
                    }
                    add {
                        paramTypes(StringClass, BooleanType)
                        returnType(BooleanClass)
                    }
                }
            }
        }.apply {
            checkDataList("HookConfigAbility")
            first().name.toClass().apply {
                method { param(StringClass, BooleanType);returnType = BooleanClass }.hook {
                    after {
                        when (args().first().string()) {
                            "is_oneplus_brand" -> if (notOplus) resultFalse()
                            "feature_is_support_watermark" -> if (waterMark) resultTrue()
                            "feature_is_support_hassel_watermark" -> if (waterMark) resultTrue()
                            "feature_is_support_photo_editor_watermark" -> if (waterMark) resultTrue()
                            "feature_is_support_privacy_watermark" -> if (waterMark) resultTrue()
                            "feature_is_support_lns" -> if (lnsImage) resultTrue()
                        }
                    }
                }
            }
        }
    }
}