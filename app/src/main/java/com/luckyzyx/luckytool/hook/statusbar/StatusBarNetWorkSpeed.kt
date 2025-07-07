package com.luckyzyx.luckytool.hook.statusbar

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.TrafficStats
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import org.lsposed.lsparanoid.Obfuscate
import kotlin.math.pow

@Obfuscate
@Suppress("MemberVisibilityCanBePrivate")
object StatusBarNetWorkSpeed : YukiBaseHooker() {

    override fun onHook() {
        loadHooker(NetWorkSpeedDelay)
        loadHooker(NetWorkSpeedView)
    }

    @Obfuscate
    object NetWorkSpeedDelay : YukiBaseHooker() {
        override fun onHook() {
            var networkSpeed = prefs(ModulePrefs).getBoolean("set_network_speed", false)
            dataChannel.wait<Boolean>("set_network_speed") { networkSpeed = it }

            //Search postUpdateNetworkSpeedDelay
            VariousClass(
                "com.oplusos.systemui.statusbar.controller.NetworkSpeedController",
                "com.oplus.systemui.statusbar.phone.netspeed.OplusNetworkSpeedControllExImpl", //C13
                "com.oplus.systemui.statusbar.phone.netspeed.OplusNetworkSpeedControllerExImpl" //C14 C15
            ).load(appClassLoader).resolve().apply {
                val bgHandler = firstField { name = "bgHandler" }
                val uiHandler = firstField { name = "uiHandler" }
                val lastTime = firstField { name = "lastTime" }
                val lastTotalBytes = firstField { name = "lastTotalBytes" }

                (firstMethodOrNull { name = "updateNetworkSpeed" }
                    ?: firstMethod { name { it.contains("updateNetworkSpeed") } }).hook {
                    before {
                        if (!networkSpeed) return@before
                        val instance = instanceOrNull ?: args().first().any()

                        val obtain = Message.obtain()
                        obtain.what = 100000
                        var finalTotal = 0L

                        val isConnected = firstField { name = "isConnected" }.of(instance)
                            .get<Boolean>() ?: false
                        val isSwitchOn = firstField { name = "isSwitchOn" }.of(instance)
                            .get<Boolean>() ?: false

                        if (isConnected && isSwitchOn) {
                            val currentTimeMillis = System.currentTimeMillis()
                            var totalByte = firstMethod { name = "getTotalByte" }.of(instance)
                                .invoke<Long>() ?: 0
                            if (totalByte <= 0) {
                                lastTime.copy().of(instance).set(0L)
                                lastTotalBytes.copy().of(instance).set(0L)
                                totalByte = firstMethod { name = "getTotalByte" }.of(instance)
                                    .invoke<Long>() ?: 0
                            }
                            val time = lastTime.copy().of(instance).get<Long>() ?: 0
                            if (time in 0..<currentTimeMillis) {
                                val totalBytes = lastTotalBytes.copy().of(instance).get<Long>() ?: 0
                                if (totalBytes > 0 && totalByte > 0 && totalByte > totalBytes) {
                                    finalTotal =
                                        ((totalByte - totalBytes) * 1000) / (currentTimeMillis - time)
                                }
                            }
                            obtain.arg1 = 1
                            obtain.obj = finalTotal
                            uiHandler.copy().of(instance).get<Handler>()?.removeMessages(100000)
                            uiHandler.copy().of(instance).get<Handler>()?.sendMessage(obtain)
                            lastTime.copy().of(instance).set(currentTimeMillis)
                            lastTotalBytes.copy().of(instance).set(totalByte)
                            bgHandler.copy().of(instance).get<Handler>()?.removeMessages(100001)
                            bgHandler.copy().of(instance).get<Handler>()
                                ?.sendEmptyMessageDelayed(100001, 1000L)
                        } else {
                            obtain.arg1 = 0
                            uiHandler.copy().of(instance).get<Handler>()?.removeMessages(100000)
                            uiHandler.copy().of(instance).get<Handler>()?.sendMessage(obtain)
                            lastTime.copy().of(instance).set(0L)
                            lastTotalBytes.copy().of(instance).set(0L)
                        }
                        resultNull()
                    }
                }
            }
        }
    }

    @Obfuscate
    object NetWorkSpeedView : YukiBaseHooker() {
        var layoutMode = prefs(ModulePrefs).getString("statusbar_network_layout", "0")
        var userTypeface = prefs(ModulePrefs).getBoolean("statusbar_network_user_typeface", false)
        var useBoldFont =
            prefs(ModulePrefs).getBoolean("statusbar_network_use_bold_font_style", false)
        var noSpace = prefs(ModulePrefs).getBoolean("statusbar_network_no_space", false)
        var noSecond = prefs(ModulePrefs).getBoolean("statusbar_network_no_second", false)
        var noUnit = prefs(ModulePrefs).getBoolean("statusbar_network_no_unit", false)
        var getDoubleSize = prefs(ModulePrefs).getInt("set_network_speed_font_size", 7)
        var getBottomPadding = prefs(ModulePrefs).getInt("set_network_speed_padding_bottom", 0)
        var setInterval = prefs(ModulePrefs).getInt("set_network_speed_double_row_spacing", -1)

        var bMargin = 0
        var tMargin = 0

        @SuppressLint("DiscouragedApi")
        override fun onHook() {
            dataChannel.wait<String>("statusbar_network_layout") { layoutMode = it }
            dataChannel.wait<Boolean>("statusbar_network_user_typeface") { userTypeface = it }
            dataChannel.wait<Boolean>("statusbar_network_use_bold_font_style") { useBoldFont = it }
            dataChannel.wait<Boolean>("statusbar_network_no_space") { noSpace = it }
            dataChannel.wait<Boolean>("statusbar_network_no_second") { noSecond = it }
            dataChannel.wait<Boolean>("statusbar_network_no_unit") { noUnit = it }
            dataChannel.wait<Int>("set_network_speed_font_size") { getDoubleSize = it }
            dataChannel.wait<Int>("set_network_speed_padding_bottom") { getBottomPadding = it }
            dataChannel.wait<Int>("set_network_speed_double_row_spacing") { setInterval = it }

            val NetworkSpeedIconState =
                "com.oplus.systemui.statusbar.phone.netspeed.NetworkSpeedIconState"

            //Source NetworkSpeedView
            VariousClass(
                "com.oplusos.systemui.statusbar.widget.NetworkSpeedView",
                "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView" //C14 C15
            ).load(appClassLoader).resolve().apply {
                val mState = firstField { type = NetworkSpeedIconState }
                val mBlocked = firstFieldOrNull { name = "mBlocked" }
                val mSpeed = firstField { name = "mSpeed" }
                val mSpeedNumber = firstField { name = "mSpeedNumber" }
                val mSpeedUnit = firstField { name = "mSpeedUnit" }
                val mDefaultBoldFont = firstField { type = Typeface::class }

                firstMethod { name = "onFinishInflate" }.hook {
                    after {
                        val viewGroup = instance<ViewGroup>()
                        //5.34dp
                        if (bMargin <= 0) bMargin = viewGroup.resources.getDimensionPixelSize(
                            viewGroup.resources.getIdentifier(
                                "network_speed_number_margin_bottom",
                                "dimen", this@NetWorkSpeedView.packageName
                            )
                        )
                        //7.34dp
                        if (tMargin <= 0) tMargin = viewGroup.resources.getDimensionPixelSize(
                            viewGroup.resources.getIdentifier(
                                "network_speed_unit_margin_top",
                                "dimen", this@NetWorkSpeedView.packageName
                            )
                        )
                    }
                }
                if (layoutMode == "0") {
                    firstMethod { name = "applyNetworkState" }.hook {
                        after {
                            val defaultBoldTypeface =
                                mDefaultBoldFont.copy().of(instance).get<Typeface>()
                            val mSpeedNumberTv = mSpeedNumber.copy().of(instance).get<TextView>()
                            val mSpeedUnitTv = mSpeedUnit.copy().of(instance).get<TextView>()
                            mSpeedNumberTv?.typeface = if (userTypeface) {
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            } else defaultBoldTypeface
                            mSpeedUnitTv?.typeface = if (userTypeface) {
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            } else defaultBoldTypeface
                        }
                    }
                } else {
                    firstMethod { name = "applyNetworkState" }.hook {
                        before {
                            if (layoutMode == "0") return@before

                            val viewGroup = instance<ViewGroup>()
                            val state = args().first().any()
                            if (state == null) {
                                viewGroup.isVisible = false
                                mState.copy().of(instance).set(null)
                                return@before
                            }

                            val copy = state.asResolver().firstMethod { name = "copy" }.invoke()
                            val getVisible = state.asResolver().firstMethod { name = "getVisible" }
                                .invoke<Boolean>() ?: false
                            mState.copy().of(instance).set(copy)
                            val block = mBlocked?.copy()?.of(instance)?.get<Boolean>() ?: false
                            val visible = getVisible && !block
                            if (visible != viewGroup.isVisible) viewGroup.isVisible = visible
                            if (!visible) return@before

                            val speedText = state.asResolver().firstMethod { name = "getSpeedText" }
                                .invoke<Long>() ?: 0
                            if (speedText < 0 || speedText > 1024.0.pow(5.0) * 1000.0) {
                                return@before
                            }
                            mSpeed.copy().of(instance).set(speedText)

                            viewGroup.apply {
                                layoutParams?.width = LayoutParams.WRAP_CONTENT
                                setPadding(0, 0, 0, getBottomPadding.dp)
                            }

                            val defaultBoldTypeface =
                                mDefaultBoldFont.copy().of(instance).get<Typeface>()
                            val mSpeedNumberTv = mSpeedNumber.copy().of(instance).get<TextView>()
                            val mSpeedUnitTv = mSpeedUnit.copy().of(instance).get<TextView>()
                            mSpeedNumberTv?.typeface = if (userTypeface) {
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            } else defaultBoldTypeface
                            mSpeedUnitTv?.typeface = if (userTypeface) {
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            } else defaultBoldTypeface

                            when (layoutMode) {
                                "1" -> {
                                    mSpeedUnitTv?.visibility = View.INVISIBLE

                                    mSpeedNumberTv?.apply {
                                        text = getTotalFormatSpeed(
                                            speedText.toFloat(), noSpace, noUnit, noSecond
                                        )
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_DIP, getDoubleSize.toFloat() * 2
                                        )
                                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                                        layoutParams = LayoutParams(layoutParams).apply {
                                            height = LayoutParams.MATCH_PARENT
                                        }
                                    }
                                }

                                "2" -> {
                                    mSpeedUnitTv?.visibility = View.VISIBLE

                                    mSpeedNumberTv?.apply {
                                        text = getTotalFormatSpeed(
                                            calcTotalTx().toFloat(), noSpace, noUnit, noSecond
                                        )
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_DIP, getDoubleSize.toFloat()
                                        )
                                        if (setInterval != -1) layoutParams =
                                            LayoutParams(layoutParams).apply {
                                                bottomMargin = bMargin + (setInterval.dp / 2)
                                            }
                                    }
                                    mSpeedUnitTv?.apply {
                                        text = getTotalFormatSpeed(
                                            calcTotalRx().toFloat(), noSpace, noUnit, noSecond
                                        )
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_DIP, getDoubleSize.toFloat()
                                        )
                                        if (setInterval != -1) layoutParams =
                                            LayoutParams(layoutParams).apply {
                                                topMargin = tMargin + (setInterval.dp / 2)
                                            }
                                    }
                                }
                            }
                            viewGroup.requestLayout()
                            resultNull()
                        }
                    }
                }
            }
        }

        var lastTxTime = 0L
        var lastTxTotalBytes = 0L

        private fun calcTotalTx(): Long {
            var totalTx = 0L
            val currentTimeMillis = System.currentTimeMillis()
            var totalByte = getTotalTxByte()
            if (totalByte <= 0) {
                lastTxTime = 0L
                lastTxTotalBytes = 0L
                totalByte = getTotalTxByte()
            }
            if (lastTxTime in 0..<currentTimeMillis) {
                if (lastTxTotalBytes > 0 && totalByte > 0 && totalByte > lastTxTotalBytes) {
                    totalTx =
                        ((totalByte - lastTxTotalBytes) * 1000) / (currentTimeMillis - lastTxTime)
                }
            }
            lastTxTime = currentTimeMillis
            lastTxTotalBytes = totalByte
            return totalTx
        }

        var lastRxTime = 0L
        var lastRxTotalBytes = 0L

        private fun calcTotalRx(): Long {
            var totalRx = 0L
            val currentTimeMillis = System.currentTimeMillis()
            var totalByte = getTotalRxByte()
            if (totalByte <= 0) {
                lastRxTime = 0L
                lastRxTotalBytes = 0L
                totalByte = getTotalRxByte()
            }
            if (lastRxTime in 0..<currentTimeMillis) {
                if (lastRxTotalBytes > 0 && totalByte > 0 && totalByte > lastRxTotalBytes) {
                    totalRx =
                        ((totalByte - lastRxTotalBytes) * 1000) / (currentTimeMillis - lastRxTime)
                }
            }
            lastRxTime = currentTimeMillis
            lastRxTotalBytes = totalByte
            return totalRx
        }

        private fun getTotalTxByte(): Long {
            val totalTxBytes = TrafficStats.getTotalTxBytes()
            val bytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val txBytes = TrafficStats.getTxBytes("lo")
                totalTxBytes - txBytes
            } else {
                totalTxBytes
            }
            return if (bytes >= 0L) bytes else 0L
        }

        private fun getTotalRxByte(): Long {
            val totalRxBytes = TrafficStats.getTotalRxBytes()
            val bytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val rxBytes = TrafficStats.getRxBytes("lo")
                totalRxBytes - rxBytes
            } else {
                totalRxBytes
            }
            return if (bytes >= 0L) bytes else 0L
        }

        private fun getTotalFormatSpeed(
            bytes: Float, noSpace: Boolean, noUnit: Boolean, noSecond: Boolean
        ): String {
            var type = 0
            val format = try {
                if (bytes >= (1024 * 1024)) {
                    type = 2
                    "%.1f".format(bytes / (1024 * 1024))
                } else if (bytes >= 1024) {
                    type = 1
                    "%.1f".format(bytes / 1024)
                } else {
                    type = 0
                    "%.1f".format(bytes)
                }
            } catch (_: Throwable) {
                "0.0"
            }
            return format + (if (noSpace) "" else " ") +
                    if (noUnit) "" else when (type) {
                        0 -> "B"
                        1 -> "KB"
                        2 -> "MB"
                        else -> "B"
                    } + if (noSecond) "" else "/s"
        }
    }
}