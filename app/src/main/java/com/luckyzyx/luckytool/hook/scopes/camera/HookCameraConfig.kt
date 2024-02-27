package com.luckyzyx.luckytool.hook.scopes.camera

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.safeOfNull

object HookCameraConfig : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookCameraVendorTag)
    }

    private object HookCameraVendorTag : YukiBaseHooker() {
        override fun onHook() {
            val is10bit = prefs(ModulePrefs).getBoolean("enable_10_bit_image_support", false)
            val isFrame = prefs(ModulePrefs).getBoolean("enable_frame_watermark_style", false)
            val isHasselblad =
                prefs(ModulePrefs).getBoolean("enable_hasselblad_watermark_style", false)
            val masterFilter = prefs(ModulePrefs).getBoolean("enable_master_filter", false)
            val jiangwenFilter = prefs(ModulePrefs).getBoolean("enable_jiangwen_filter", false)
            val grandTourFilter = prefs(ModulePrefs).getBoolean("enable_grand_tour_filter", false)

            val portraitFilters =
                prefs(ModulePrefs).getStringSet("camera_portrait_filter_settings", ArraySet())
            val videoFilters =
                prefs(ModulePrefs).getStringSet("camera_video_filter_settings", ArraySet())

            //Source CameraUnitUtils
            "com.oplus.ocs.camera.appinterface.adapter.CameraUnitUtils".toClass().apply {
                method { name = "getVendorTagConfig" }.hook {
                    after {
                        when (@Suppress("UNUSED_VARIABLE") val key = args().first().string()) {
                            //<string name="camera_heic_encode_10bits_title">10 亿色影像</string>
                            //OptionKey PRE_KEY_10BIT_HEIC_ENCODE pref_10bits_heic_encode_key
                            "com.oplus.10bits.heic.encode.support" -> if (is10bit) result(true)
                            "com.oplus.feature.video.10bit.support" -> if (is10bit) result(true)

                            //Source SloganUtil -> Shot on OnePlus / Hasselblad
                            "com.oplus.hasselblad.watermark.support.default" -> if (isHasselblad) result(
                                true
                            )
                            //通用哈苏水印
                            "com.oplus.camera.support.custom.hasselblad.watermark" -> if (isHasselblad) result(
                                true
                            )
                            //哈苏水印指南
                            "com.oplus.hasselblad.watermark.guide.support" -> if (isHasselblad) result(
                                true
                            )
                            //<string name="camera_hasselblad_watermark_setting_title_str">哈苏定制水印</string>
                            //OptionKey PRE_KEY_WATERMARK_HASSELBLAD / pref_hasselblad_watermark_function_key
                            "com.oplus.use.hasselblad.style.support" -> if (isHasselblad) result(
                                true
                            )

                            //画框水印
                            "com.oplus.camera.support.frame.watermark" -> if (isFrame) result(true)
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

                            //Filter

                            //大师滤镜
                            "com.oplus.photo.master.filter.type.list", "com.oplus.portrait.master.filter.type.list" -> if (isHasselblad && masterFilter) {
                                result("Radiance.cube.rgb.bin,Serenity.cube.rgb.bin,Emerald.cube.rgb.bin")
                            }

                            //姜文滤镜
                            "com.oplus.director.filter.support" -> if (jiangwenFilter) result(true)
                            "com.oplus.director.filter.rus" -> if (jiangwenFilter) result(true)
                            "com.oplus.director.filter.upgrade.support" -> if (jiangwenFilter) result(
                                true
                            )

                            //盛大旅行
                            "com.oplus.support.grand.tour.filter" -> if (grandTourFilter) result(
                                true
                            )
//                            "com.oplus.street.grand.tour.filter.type.support" -> result(true)

                            //Filter Portrait
                            //流光人像
//                            "com.oplus.feature.portrait.streamer.support" -> result(true)
                            //人像留色
                            "com.oplus.feature.portrait.retention.support" -> if (
                                portraitFilters.contains("retention")) result(true)

                            "com.oplus.feature.portrait.front.retention.support" -> if (
                                portraitFilters.contains("retention")) result(true)

                            "com.oplus.feature.portrait.back.retention.support" -> if (
                                portraitFilters.contains("retention")) result(true)
                            //光斑人像
                            "com.oplus.feature.portrait.neon.support" -> if (
                                portraitFilters.contains("bokeh_flare_portrait")) result(true)

                            "com.oplus.feature.portrait.neon.front.support" -> if (
                                portraitFilters.contains("bokeh_flare_portrait")) result(true)

                            //Filter Video
                            //赤红/森绿/天蓝
                            "com.oplus.video.color_extraction.support" -> if (
                                videoFilters.contains("color_extraction")) result(true)
                            //人像留色
                            "com.oplus.video.retention.support" -> if (
                                videoFilters.contains("retention")) result(true)
                            //光斑人像
                            "com.oplus.video.neon.support" -> if (
                                videoFilters.contains("bokeh_flare_portrait")) result(true)

                            "com.oplus.video.only.blur.support" -> if (
                                videoFilters.contains("bokeh_flare_portrait")) result(true)

                        }
                    }
                }
            }
        }
    }

    private fun HookParam.result(value: Any?) {
        result = when (value) {
            is Boolean -> if (value) "1" else "0"
            is Int -> value.toString()
            else -> value
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
}