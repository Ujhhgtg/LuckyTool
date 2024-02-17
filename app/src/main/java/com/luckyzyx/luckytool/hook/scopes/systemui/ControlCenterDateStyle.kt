package com.luckyzyx.luckytool.hook.scopes.systemui

import android.text.TextUtils
import android.util.LayoutDirection
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.utils.sysui.LunarHelperUtils
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getScreenOrientation
import java.util.Locale
import kotlin.math.abs

@Suppress("LocalVariableName", "DiscouragedApi")
object ControlCenterDateStyle : YukiBaseHooker() {
    override fun onHook() {
        var removeComma = prefs(ModulePrefs).getBoolean("remove_control_center_date_comma", false)
        dataChannel.wait<Boolean>("remove_control_center_date_comma") { removeComma = it }
        var showLunar =
            prefs(ModulePrefs).getBoolean("statusbar_control_center_date_show_lunar", false)
        dataChannel.wait<Boolean>("statusbar_control_center_date_show_lunar") { showLunar = it }
        var disableScroll = prefs(ModulePrefs).getBoolean(
            "statusbar_control_center_date_disable_text_scroll", false
        )
        dataChannel.wait<Boolean>("statusbar_control_center_date_disable_text_scroll") {
            disableScroll = it
        }
        var displayMode = prefs(ModulePrefs).getString(
            "statusbar_control_center_date_set_display_mode_horizontal",
            "0"
        )
        dataChannel.wait<String>("statusbar_control_center_date_set_display_mode_horizontal") {
            displayMode = it
        }

        var lunarInstance: Any? = null

        //Source OplusQSDateView
        VariousClass(
            "com.oplusos.systemui.qs.widget.OplusQSDateView", //C13
            "com.oplus.systemui.qs.widget.OplusQSDateView" //C14
        ).toClass().apply {
            method { name = "updateClock";emptyParam() }.hook {
                after {
                    if (!removeComma && !showLunar) return@after
                    val dateView = instance<TextView>()
                    var res = dateView.text as String
                    if (removeComma) res = res.replace("，", " ")
                    if (showLunar) {
                        LunarHelperUtils(appClassLoader).apply {
                            if (lunarInstance == null) lunarInstance = getInstance(dateView.context)
                            val lunarInfo = generateLunarDate(lunarInstance, 2)
                            if (lunarInfo.isNotBlank()) res += " $lunarInfo" else return@after
                        }
                    }
                    dateView.text = res
                    field { name = "mLastText" }.get(instance).set(res)
                }
            }
        }

        if (SDK < A13) return
        var translationX = 0
        //Source OplusQSFooterImpl
        VariousClass(
            "com.oplusos.systemui.qs.OplusQSFooterImpl", //C13
            "com.oplus.systemui.qs.OplusQSFooterImpl" //C14
        ).toClass().apply {
            if (hasMethod { name = "updateQsDateView" }.not()) return@apply
            method { name = "updateQsDateView" }.hook {
                after {
                    val res = instance<ViewGroup>().resources
                    val mTmpConstraintSet =
                        field { name = "mTmpConstraintSet" }.get(instance).any()
                            ?: return@after
                    val mClockView = field { name = "mClockView" }.get(instance).cast<TextView>()
                        ?: return@after
                    val mQsDateView = field { name = "mQsDateView" }.get(instance).cast<TextView>()
                        ?: return@after

                    if (disableScroll) mTmpConstraintSet.current().method {
                        name = "constrainWidth"
                    }.call(mQsDateView.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)

                    if (showLunar && (displayMode != "0")) {
                        //162dp
                        val qs_footer_date_width = res.getDimensionPixelSize(
                            res.getIdentifier(
                                "qs_footer_date_width", "dimen",
                                ControlCenterDateStyle.packageName
                            )
                        )
                        //10dp
                        val qs_footer_date_margin_start = res.getDimensionPixelSize(
                            res.getIdentifier(
                                "qs_footer_date_margin_start", "dimen",
                                ControlCenterDateStyle.packageName
                            )
                        )
                        //51dp
                        val qs_footer_date_expand_translation_y = res.getDimensionPixelSize(
                            res.getIdentifier(
                                "qs_footer_date_expand_translation_y", "dimen",
                                ControlCenterDateStyle.packageName
                            )
                        )
                        val isRtl =
                            TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL
                        val width = mClockView.width + qs_footer_date_margin_start
                        if (abs(translationX) < abs(width)) translationX =
                            if (!isRtl) (-width) else width
                        val translationY = qs_footer_date_expand_translation_y / 2

                        getScreenOrientation(res) {
                            if (it) return@getScreenOrientation
                            if (translationX == 0 || translationY == 0) return@getScreenOrientation

                            when (displayMode) {
                                "1" -> mTmpConstraintSet.current().method {
                                    name = "constrainWidth"
                                }.call(mQsDateView.id, qs_footer_date_width * 2)

                                "2" -> {
                                    mTmpConstraintSet.current().method {
                                        name = "constrainWidth"
                                    }.call(
                                        mQsDateView.id, ConstraintLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    mTmpConstraintSet.current().method {
                                        name = "setTranslationX"
                                    }.call(mQsDateView.id, translationX.toFloat())
                                    mTmpConstraintSet.current().method {
                                        name = "setTranslationY"
                                    }.call(mQsDateView.id, translationY.toFloat())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}