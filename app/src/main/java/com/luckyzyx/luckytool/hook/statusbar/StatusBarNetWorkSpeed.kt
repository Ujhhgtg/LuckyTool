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
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.TypefaceClass
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
            ).toClass().apply {
                val bgHandler = field { name = "bgHandler" }
                val uiHandler = field { name = "uiHandler" }
                val lastTime = field { name = "lastTime" }
                val lastTotalBytes = field { name = "lastTotalBytes" }

                val hasNetworkSpeed = hasMethod { name = "updateNetworkSpeed" }
                method {
                    name {
                        if (hasNetworkSpeed) it == "updateNetworkSpeed"
                        else it.contains("updateNetworkSpeed")
                    }
                }.hook {
                    replaceUnit {
                        val instance = instanceOrNull ?: args().first().any()

                        if (!networkSpeed) {
                            callOriginal()
                            return@replaceUnit
                        }

                        val obtain = Message.obtain()
                        obtain.what = 100000
                        var finalTotal = 0L

                        val isConnected = field { name = "isConnected" }.get(instance).boolean()
                        val isSwitchOn = field { name = "isSwitchOn" }.get(instance).boolean()

                        if (isConnected && isSwitchOn) {
                            val currentTimeMillis = System.currentTimeMillis()
                            var totalByte = method { name = "getTotalByte" }.get(instance).long()
                            if (totalByte <= 0) {
                                lastTime.get(instance).set(0L)
                                lastTotalBytes.get(instance).set(0L)
                                totalByte = method { name = "getTotalByte" }.get(instance).long()
                            }
                            val time = lastTime.get(instance).long()
                            if (time in 0..<currentTimeMillis) {
                                val totalBytes = lastTotalBytes.get(instance).long()
                                if (totalBytes > 0 && totalByte > 0 && totalByte > totalBytes) {
                                    finalTotal =
                                        ((totalByte - totalBytes) * 1000) / (currentTimeMillis - time)
                                }
                            }
                            obtain.arg1 = 1
                            obtain.obj = finalTotal
                            uiHandler.get(instance).cast<Handler>()?.removeMessages(100000)
                            uiHandler.get(instance).cast<Handler>()?.sendMessage(obtain)
                            lastTime.get(instance).set(currentTimeMillis)
                            lastTotalBytes.get(instance).set(totalByte)
                            bgHandler.get(instance).cast<Handler>()?.removeMessages(100001)
                            bgHandler.get(instance).cast<Handler>()
                                ?.sendEmptyMessageDelayed(100001, 1000L)
                            return@replaceUnit
                        }
                        obtain.arg1 = 0
                        uiHandler.get(instance).cast<Handler>()?.removeMessages(100000)
                        uiHandler.get(instance).cast<Handler>()?.sendMessage(obtain)
                        lastTime.get(instance).set(0L)
                        lastTotalBytes.get(instance).set(0L)
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
            dataChannel.wait<Int>("set_network_speed_font_size") { getDoubleSize = it }
            dataChannel.wait<Int>("set_network_speed_padding_bottom") { getBottomPadding = it }
            dataChannel.wait<Int>("set_network_speed_double_row_spacing") { setInterval = it }

            val NetworkSpeedIconState =
                "com.oplus.systemui.statusbar.phone.netspeed.NetworkSpeedIconState"

            //Source NetworkSpeedView
            VariousClass(
                "com.oplusos.systemui.statusbar.widget.NetworkSpeedView",
                "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView" //C14 C15
            ).toClass().apply {
                val mState = field { type = NetworkSpeedIconState }
                val mBlocked = field { name = "mBlocked" }
                val mSpeed = field { name = "mSpeed" }
                val mSpeedNumber = field { name = "mSpeedNumber" }
                val mSpeedUnit = field { name = "mSpeedUnit" }
                val mDefaultBoldFont = field { type = TypefaceClass }

                method { name = "onFinishInflate" }.hook {
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
                method { name = "applyNetworkState" }.hook {
                    replaceUnit {
                        if (layoutMode == "0") {
                            callOriginal()
                            return@replaceUnit
                        }

                        val viewGroup = instance<ViewGroup>()
                        val state = args().first().any()
                        if (state == null) {
                            viewGroup.isVisible = false
                            mState.get(instance).setNull()
                            return@replaceUnit
                        }

                        val copy = state.current().method { name = "copy" }.call()
                        val getVisible = state.current().method { name = "getVisible" }.boolean()
                        mState.get(instance).set(copy)
                        val visible = getVisible && !mBlocked.get(instance).boolean()
                        if (visible != viewGroup.isVisible) viewGroup.isVisible = visible
                        if (!visible) return@replaceUnit

                        val speedText = state.current().method { name = "getSpeedText" }.long()
                        if (speedText < 0 || speedText > 1024.0.pow(5.0) * 1000.0) {
                            return@replaceUnit
                        }
                        mSpeed.get(instance).set(speedText)

                        viewGroup.apply {
                            layoutParams?.width = LayoutParams.WRAP_CONTENT
                            setPadding(0, 0, 0, getBottomPadding.dp)
                        }

                        val defaultBoldTypeface = mDefaultBoldFont.get(instance).cast<Typeface>()
                        val mSpeedNumberTv = mSpeedNumber.get(instance).cast<TextView>()
                        val mSpeedUnitTv = mSpeedUnit.get(instance).cast<TextView>()
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
                                        speedText.toFloat(), noSpace, noSecond
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
                                        calcTotalTx().toFloat(), noSpace, noSecond
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
                                        calcTotalRx().toFloat(), noSpace, noSecond
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

        private fun getTotalFormatSpeed(bytes: Float, noSpace: Boolean, noSecond: Boolean): String {
            var format = try {
                if (bytes >= (1024 * 1024)) {
                    "%.1f MB/s".format(bytes / (1024 * 1024))
                } else if (bytes >= 1024) {
                    "%.1f KB/s".format(bytes / 1024)
                } else {
                    "%.1f B/s".format(bytes)
                }
            } catch (t: Throwable) {
                "0.0 B/s"
            }
            if (noSpace) format = format.replace(" ", "")
            if (noSecond) format = format.replace("/s", "")
            return format
        }
    }
}