package com.luckyzyx.luckytool.hook.scopes.camera

import android.util.ArrayMap
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.safeOfNull

object HookCameraConfig : YukiBaseHooker() {
    override fun onHook() {
        val list = ArrayMap<String, Any>().apply {
            //10亿色影像
            if (prefs(ModulePrefs).getBoolean("enable_10_bit_image_support", false)) {
                put("com.oplus.10bits.heic.encode.support", true)
                put("com.oplus.feature.video.10bit.support", true)
            }
            //画框水印
            if (prefs(ModulePrefs).getBoolean("enable_frame_watermark_style", false)) {
                put("com.oplus.camera.support.frame.watermark", true)
            }

            //画框颜色 色卡贴纸
//            put("com.oplus.camera.support.custom.color.watermark", true)
//            put("com.oplus.camera.support.color.extraction", true)

            //哈苏水印
            if (prefs(ModulePrefs).getBoolean("enable_hasselblad_watermark_style", false)) {
                //Source SloganUtil -> Shot on OnePlus / Hasselblad
                put("com.oplus.hasselblad.watermark.support.default", true)
                //通用哈苏水印
                put("com.oplus.camera.support.custom.hasselblad.watermark", true)
                //哈苏水印指南
                put("com.oplus.hasselblad.watermark.guide.support", true)
                //<string name="camera_hasselblad_watermark_setting_title_str">哈苏定制水印</string>
                //OptionKey PRE_KEY_WATERMARK_HASSELBLAD / pref_hasselblad_watermark_function_key
                put("com.oplus.use.hasselblad.style.support", true)
            }

            //Filter FilterGroupManager 大师滤镜
            if (prefs(ModulePrefs).getBoolean("enable_master_filter", false)) {
                put(
                    "com.oplus.photo.master.filter.type.list",
                    "Radiance.cube.rgb.bin,Serenity.cube.rgb.bin,Emerald.cube.rgb.bin"
                )
                put(
                    "com.oplus.portrait.master.filter.type.list",
                    "Radiance.cube.rgb.bin,Serenity.cube.rgb.bin,Emerald.cube.rgb.bin"
                )
            }
            //姜文滤镜
            if (prefs(ModulePrefs).getBoolean("enable_jiangwen_filter", false)) {
                put("com.oplus.director.filter.support", true)
                put("com.oplus.director.filter.rus", true)
                put("com.oplus.director.filter.upgrade.support", true)
            }
            //盛大旅行滤镜
            if (prefs(ModulePrefs).getBoolean("enable_grand_tour_filter", false)) {
                put("com.oplus.support.grand.tour.filter", true)
//                "com.oplus.street.grand.tour.filter.type.support" -> result(true)
            }

            //Filter Portrait
            val portraitFilters =
                prefs(ModulePrefs).getStringSet("camera_portrait_filter_settings", ArraySet())
            //流光人像
//                "com.oplus.feature.portrait.streamer.support" -> result(true)
            //人像留色
            if (portraitFilters.contains("retention")) {
                put("com.oplus.feature.portrait.retention.support", true)
                put("com.oplus.feature.portrait.front.retention.support", true)
                put("com.oplus.feature.portrait.back.retention.support", true)
            }
            //光斑人像
            if (portraitFilters.contains("bokeh_flare_portrait")) {
                put("com.oplus.feature.portrait.neon.support", true)
                put("com.oplus.feature.portrait.neon.front.support", true)
            }

            //Filter Video
            val videoFilters =
                prefs(ModulePrefs).getStringSet("camera_video_filter_settings", ArraySet())

            //赤红/森绿/天蓝
            if (videoFilters.contains("color_extraction")) {
                put("com.oplus.video.color_extraction.support", true)
            }
            //人像留色
            if (videoFilters.contains("retention")) {
                put("com.oplus.video.retention.support", true)
            }
            //光斑人像
            if (videoFilters.contains("bokeh_flare_portrait")) {
                put("com.oplus.video.neon.support", true)
                put("com.oplus.video.only.blur.support", true)
            }
        }
        loadHooker(HookCameraVendorTag(list))
    }
}

private class HookCameraVendorTag(val tags: Map<String, Any>) : YukiBaseHooker() {
    override fun onHook() {
        //Source CameraUnitUtils
        "com.oplus.ocs.camera.appinterface.adapter.CameraUnitUtils".toClass().apply {
            method { name = "getVendorTagConfig" }.hook {
                after {
                    val key = args().first().string()
                    if (key.isBlank()) return@after
                    val value = tags[key] ?: return@after
                    result = when (value) {
                        is Boolean -> if (value) "1" else "0"
                        is Int -> value.toString()
                        else -> value
                    }

                    when (key) {
                        //孤独星球
                        "com.oplus.camera.support.custom.lonely.planet.watermark" -> {}
                        //美妆定制水印
                        //res/layout/camera_watermark_makeup_visual_layout.xml
                        //imageView_watermark_makeup_visual
                        //key PRE_KEY_WATERMARK_MAKEUP / pref_watermark_makeup_function_key
                        //is_slogan
                        "com.oplus.feature.custom.makeup.watermark.support" -> {}

                        //大师模式
                        "com.oplus.feature.master.mode.support" -> {}
                        "com.oplus.feature.master.ui.mode.support" -> {}
                        "com.oplus.feature.master.mode.professional.name" -> {}

                        //街拍模式
                        "com.oplus.feature.street.mode.support" -> {}

                        //陆川滤镜 / 光影有声
//                            "com.oplus.tol.style.filter.support" -> result(true)

                        //录像轮盘变焦
//                        "com.oplus.video.inertial.zoom.support" -> result = "1"

                    }
                }
            }
        }
    }
}

@Suppress("unused")
private object HookCameraConfigValue : YukiBaseHooker() {
    override fun onHook() {
        val is10bit = prefs(ModulePrefs).getBoolean("enable_10_bit_image_support", false)
        val isHasselblad =
            prefs(ModulePrefs).getBoolean("enable_hasselblad_watermark_style", false)
        val masterFilter = prefs(ModulePrefs).getBoolean("enable_master_filter", false)
        val jiangwenFilter = prefs(ModulePrefs).getBoolean("enable_jiangwen_filter", false)

        //Source CameraConfig
        VariousClass(
            "com.oplus.camera.aps.config.CameraConfig", //C12
            "com.oplus.camera.configure.CameraConfig" //C13
        ).toClass().apply {
            method { name = "getConfigBooleanValue";paramCount = 1 }.hook {
                after {
                    when (args().first().string()) {
                        //<string name="camera_heic_encode_10bits_title">10 亿色影像</string>
                        //OptionKey PRE_KEY_10BIT_HEIC_ENCODE pref_10bits_heic_encode_key
                        "com.oplus.10bits.heic.encode.support" -> if (is10bit) resultTrue()
                        "com.oplus.feature.video.10bit.support" -> if (is10bit) resultTrue()

                        //Source SloganUtil -> Shot on OnePlus / Hasselblad
                        "com.oplus.hasselblad.watermark.support.default" -> if (isHasselblad) resultTrue()
                        //通用哈苏水印
                        "com.oplus.camera.support.custom.hasselblad.watermark" -> if (isHasselblad) resultTrue()
                        //<string name="camera_hasselblad_watermark_setting_title_str">哈苏定制水印</string>
                        //OptionKey PRE_KEY_WATERMARK_HASSELBLAD / pref_hasselblad_watermark_function_key
                        "com.oplus.use.hasselblad.style.support" -> if (isHasselblad) resultTrue()
                        //<string name="camera_beauty_makeup_watermark_setting_title">美妆定制水印</string>
                        //OptionKey PRE_KEY_WATERMARK_MAKEUP pref_watermark_makeup_function_key
//                        "com.oplus.feature.custom.makeup.watermark.support" -> if (isHasselblad) resultTrue()

                        //Source FilterHelper 姜文滤镜
                        "com.oplus.director.filter.support" -> if (jiangwenFilter) resultTrue()
                        "com.oplus.director.filter.rus" -> if (jiangwenFilter) resultTrue()
                        "com.oplus.director.filter.upgrade.support" -> if (jiangwenFilter) resultTrue()

                        //res/layout/camera_watermark_makeup_visual_layout.xml
                        //imageView_watermark_makeup_visual
                        //key PRE_KEY_WATERMARK_MAKEUP / pref_watermark_makeup_function_key
                        //is_slogan

                        "com.oplus.old.watermark" -> {}
                        "com.oplus.video.watermark.support" -> {}

                        "com.oplus.blur.edit.in.gallery.support" -> {} //resultTrue()
                        "com.oplus.watermark.edit.in.gallery.support" -> {} //resultTrue()

                        "com.oplus.camera.customwatermark.config.size" -> {} //resultTrue()
                        "com.oplus.watermark.is.new.project.behavior" -> {} //resultTrue()
                        "com.oplus.video.watermark.hal.support" -> {} //resultTrue()

                        "com.oplus.camera.support.custom.hasselblad.watermark.sellmode.default.open" -> {}
                    }
                }
            }
            method {
                param { it[0] == StringClass }
                paramCount(1..2)
                returnType = StringClass
            }.hookAll {
                after {
                    when (args().first().string()) {
                        //哈苏水印样式 camera_slogan_hasselblad
                        "com.oplus.use.hasselblad.style.support" -> if (isHasselblad) {
                            if (result<String>()?.toIntOrNull() != null) result = "1"
                        }
                    }
                }
            }
            method {
                param(StringClass)
                returnType = ListClass
            }.hookAll {
                after {
                    val type = safeOfNull { method.genericReturnType.typeName } ?: return@after
                    if (type.contains(StringClass.name).not()) return@after
                    when (args().first().string()) {
                        //Source FilterGroupManager 照片 / 人像 大师滤镜
                        "com.oplus.photo.master.filter.type.list", "com.oplus.portrait.master.filter.type.list" -> if (isHasselblad && masterFilter) result =
                            listOf(
                                "Radiance.cube.rgb.bin",
                                "Serenity.cube.rgb.bin",
                                "Emerald.cube.rgb.bin"
                            )
                    }
                }
            }
        }
    }
}