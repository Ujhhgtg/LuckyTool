package com.luckyzyx.luckytool.hook.scopes.gallery

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.safeOf
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookSystemStorage(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        val osCode = getOSVersionCode

        //替换OnePlus机型水印
        val notOplus = prefs(ModulePrefs).getBoolean("replace_oneplus_model_watermark", false)
        //水印编辑
        val waterMark = prefs(ModulePrefs).getBoolean("enable_watermark_editing", false)
        //高级筛选
        val seniorPicked =
            prefs(ModulePrefs).getBoolean("enable_photo_listview_senior_picked", false)
        //拇指线
        val thumbLine = prefs(ModulePrefs).getString("set_photo_view_thumb_line_display_mode", "0")
        //GIF合成
        val gifSynthesis = prefs(ModulePrefs).getBoolean("enable_photo_editor_gif_synthesis", false)
        //闪速抠图
        val lnsImage = prefs(ModulePrefs).getBoolean("enable_lns_cut_photo", false)
        //画框水印
        val frameWaterMark = prefs(ModulePrefs).getBoolean("enable_frame_watermark", false)
        //哈苏水印
        val hasselWaterMark = prefs(ModulePrefs).getBoolean("enable_hassel_watermark", false)
        //隐私水印
        val privicyWaterMark = prefs(ModulePrefs).getBoolean("enable_privacy_watermark", false)
        //新春水印
        val springFestival =
            prefs(ModulePrefs).getBoolean("enable_spring_festival_watermark", false)
        //国庆水印
        val nationalDay =
            prefs(ModulePrefs).getBoolean("enable_national_day_watermark", false)

        //Source OtherSystemStorage
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass)
                }
                methods {
                    add { paramCount(2);returnType(IntClass) }
                    add { paramCount(2);returnType(LongClass) }
                    add { paramCount(2);returnType(BooleanClass) }
                    add { paramCount(2);returnType(StringClass) }
                    add { paramCount(2);returnType(UnitType) }
                    add { paramCount(0);returnType(BooleanType) }
                    add { paramCount(4);returnType(BooleanType) }
                }
                usingStrings("configNode")
            }
        }.apply {
            checkDataList("HookSystemStorage")
            single().name.toClass().apply {
                method { param(VagueType, BooleanType);returnType = BooleanClass }.hook {
                    after {
                        val context = field { type = ContextClass }.get(instance).cast<Context>()
                            ?: return@after
                        val configNode = args().first().any().toString()
                        when {
                            //com.oplus.camera.support.custom.hasselblad.watermark
                            configNode.contains("feature_is_support_watermark") -> if (waterMark) resultTrue()
                            //com.oplus.camera.support.custom.hasselblad.watermark
                            configNode.contains("feature_is_support_hassel_watermark") -> if (hasselWaterMark) resultTrue()
                            //com.oplus.feature.custom.makeup.watermark.support
//                            configNode.contains("feature_is_support_street_watermark") -> if (waterMark) resultTrue()
                            //debug.enable.ipu
//                            configNode.contains("feature_is_support_ipu_watermark") -> if (waterMark) resultTrue()
                            //com.oplus.camera.support.custom.color.watermark
//                            configNode.contains("feature_is_support_color_watermark") -> if (waterMark) resultTrue()
                            //com.oplus.camera.support.custom.lonely.planet.watermark
//                            configNode.contains("feature_is_support_lonely_planet_watermark") -> if (waterMark) resultTrue()

                            //isRegionIndia / isInTime / is_realme_brand
//                            configNode.contains("feature_is_support_diwali_festival_watermark") -> if (waterMark) resultTrue()

                            //is_realme_brand / debug.gallery.photo.editor.watermark.switcher
                            configNode.contains("feature_is_support_photo_editor_watermark") -> if (waterMark) resultTrue()
                            //first_api_level
//                            configNode.contains("feature_is_support_photo_editor_frame_watermark") -> result =
//                                frameWaterMark

                            //is_realme_brand / debug.gallery.photo.editor.watermark.switcher
                            configNode.contains("feature_is_support_privacy_watermark") -> if (privicyWaterMark) resultTrue()
                            //os.graphic.gallery.photolistview.senior_picked
                            configNode.contains("feature_is_support_senior_picked") -> if (seniorPicked) resultTrue()
                            //debug.gallery.photo.photothumbline / os.graphic.gallery.photoview.thumb_line
                            configNode.contains("feature_is_support_photo_thumb_line") -> when (thumbLine) {
                                "1" -> resultTrue()
                                "2" -> resultFalse()
                            }
                            //debug.gallery.gif.support.synthesis / os.graphic.gallery.photoeditor.gif_synthesis
                            configNode.contains("feature_is_support_gif_synthesis") -> if (gifSynthesis) resultTrue()
                            //debug.gallery.lns / os.graphic.gallery.photoview.lns
                            configNode.contains("feature_is_support_lns") -> if (lnsImage) resultTrue()

                            //isOsSupport 29 -> 27 (Downgrade)
                            //debug.gallery.photo.editor.watermark.switcher / is_region_cn (property_domestic)
                            configNode.contains("feature_is_support_spring_festival_watermark") -> {
                                if (springFestival && osCode >= 27 && isRegionCN(context)) resultTrue()
                            }
                            //debug.gallery.photo.editor.watermark.switcher / is_region_cn (property_domestic)
                            configNode.contains("feature_is_support_national_day_watermark") -> {
                                if (nationalDay && osCode >= 27 && isRegionCN(context)) resultTrue()
                            }
                        }
                    }
                }
            }
        }

        if (hasselWaterMark) {
            //Source WatermarkInfo / WatermarkDevice
            dexKitBridge.findClass {
                matcher {
                    addFieldForType(IntType)
                    addFieldForType(BooleanType)
                    addFieldForType(StringClass)
                    usingStrings("WatermarkDevice", "isHasselDevice")
                }
            }.apply {
                checkDataList("WatermarkDevice HasselDevice", onlyOne = false)
                forEachIndexed { _, classData ->
                    classData.name.toClass().apply {
                        val hasField = hasField { type = BooleanType }
                        if (hasField) constructor { }.hookAll {
                            after {
                                field { type = BooleanType }.get(instance).setTrue()
                            }
                        }
                        else YLog.debug("WatermarkDevice HasselDevice hook error! -> ${classData.name}")
                    }
                }
            }
        }

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
            single().name.toClass().apply {
                method { param(StringClass, BooleanType);returnType = BooleanClass }.hook {
                    after {
                        when (args().first().string()) {
                            "is_oneplus_brand" -> if (notOplus) resultFalse()
//                            "feature_is_support_watermark" -> if (waterMark) resultTrue()
//                            "feature_is_support_hassel_watermark" -> if (hasselWaterMark) resultTrue()
//                            "feature_is_support_photo_editor_watermark" -> if (waterMark) resultTrue()
//                            "feature_is_support_privacy_watermark" -> if (privicyWaterMark) resultTrue()
//                            "feature_is_support_senior_picked" -> if (seniorPicked) resultTrue()
//                            "feature_is_support_photo_thumb_line" -> when (thumbLine) {
//                                "1" -> resultTrue()
//                                "2" -> resultFalse()
//                            }
//
//                            "feature_is_support_gif_synthesis" -> if (gifSynthesis) resultTrue()
//                            "feature_is_support_lns" -> if (lnsImage) resultTrue()

//                            photopage_detail_ic_dolby_vision
//                            "brighten_version_dolby" -> if (gifSynthesis) resultTrue()
//                            "feature_is_support_dolby_brighten" -> if (gifSynthesis) resultTrue()
//                            "is_support_dolby_decode" -> if (gifSynthesis) resultTrue()
//                            "is_support_dolby_encode" -> if (gifSynthesis) resultTrue()
//                            "is_support_dolby_encode_accelerate" -> if (gifSynthesis) resultTrue()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    fun isRegionCN(context: Context): Boolean {
        return safeOf(isZh(context)) {
            context.resources.getBoolean(
                context.resources.getIdentifier(
                    "property_domestic", "bool", this@HookSystemStorage.packageName
                )
            )
        }
    }
}