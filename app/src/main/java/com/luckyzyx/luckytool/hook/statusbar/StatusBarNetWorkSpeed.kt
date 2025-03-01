package com.luckyzyx.luckytool.hook.statusbar

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.TrafficStats
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.dp

@Suppress("MemberVisibilityCanBePrivate")
@Obfuscate
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
                val hasPostDelay = hasMethod { name = "postUpdateNetworkSpeedDelay" }
                val hasNetworkSpeed = hasMethod { name = "updateNetworkSpeed" }
                if (hasPostDelay) {
                    method { name = "postUpdateNetworkSpeedDelay";paramCount = 1 }.hook {
                        before {
                            if (networkSpeed && (args().first().long() == 4000L)) {
                                args().first().set(1000L)
                            }
                        }
                    }
                } else {
                    method {
                        name {
                            if (hasNetworkSpeed) it == "hasNetworkSpeed"
                            else it.contains("updateNetworkSpeed")
                        }
                    }.hook {
                        after {
                            val ins = if (args.isEmpty()) instance else args().first().any()
                            val isConnected = field { name = "isConnected" }.get(ins).boolean()
                            val isSwitchOn = field { name = "isSwitchOn" }.get(ins).boolean()
                            val bgHandler =
                                field { name = "bgHandler" }.get(ins).cast<Handler>()
                            if (isConnected && isSwitchOn) {
                                bgHandler?.removeMessages(100001)
                                bgHandler?.sendEmptyMessageDelayed(100001, 1000L)
                            }
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    object NetWorkSpeedView : YukiBaseHooker() {

        private const val LOOPBACK_IFACE = "lo"

        /** 上次总的上行流量 */
        private var lastTotalUpBytes: Long = 0L

        /** 上次总的下行流量 */
        private var lastTotalDownBytes: Long = 0L

        /** 上次总的上行时间戳 */
        private var lastTotalUpTime: Long = 0L

        /** 上次总的下行时间戳 */
        private var lastTotalDownTime: Long = 0L

        val layoutMode = prefs(ModulePrefs).getString("statusbar_network_layout", "0")
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
            dataChannel.wait<Boolean>("statusbar_network_user_typeface") { userTypeface = it }
            dataChannel.wait<Boolean>("statusbar_network_use_bold_font_style") { useBoldFont = it }
            dataChannel.wait<Boolean>("statusbar_network_no_space") { noSpace = it }
            dataChannel.wait<Boolean>("statusbar_network_no_second") { noSecond = it }
            dataChannel.wait<Int>("set_network_speed_font_size") { getDoubleSize = it }
            dataChannel.wait<Int>("set_network_speed_padding_bottom") { getBottomPadding = it }
            dataChannel.wait<Int>("set_network_speed_double_row_spacing") { setInterval = it }

            //Source NetworkSpeedView
            VariousClass(
                "com.oplusos.systemui.statusbar.widget.NetworkSpeedView",
                "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView" //C14
            ).toClass().apply {
                val hasUpdate = hasMethod { name = "updateNetworkSpeed" }
                method { name = "onFinishInflate" }.hook {
                    after {
                        val viewGroup = instance<ViewGroup>()
                        when (layoutMode) {
                            "1" -> {
                                val speedUnit: TextView? = viewGroup.findViewById(
                                    viewGroup.resources.getIdentifier(
                                        "unit", "id",
                                        this@NetWorkSpeedView.packageName
                                    )
                                )
                                viewGroup.removeView(speedUnit)
                            }
                        }
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
                method {
                    name = if (hasUpdate) "updateNetworkSpeed" else "applyNetworkState"
                }.hook {
                    before {
                        val mSpeedNumber = field { name = "mSpeedNumber" }.get(instance)
                            .cast<TextView>() ?: return@before
                        val mSpeedUnit = field { name = "mSpeedUnit" }.get(instance)
                            .cast<TextView>() ?: return@before
                        if (userTypeface) {
                            mSpeedNumber.typeface =
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            mSpeedUnit.typeface =
                                if (useBoldFont) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                        }

                        if (layoutMode == "0") return@before
                        val viewGroup = instance<ViewGroup>().apply {
                            layoutParams?.width = LayoutParams.WRAP_CONTENT
                            setPadding(0, 0, 0, getBottomPadding.dp)
                        }

                        when (layoutMode) {
                            "1" -> {
                                var speed = args().first().string()
                                if (noSecond) speed = speed.replace("/s", "")
                                if (noSpace) speed = speed.replace(" ", "")
                                mSpeedNumber.apply {
                                    text = speed
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
                                mSpeedNumber.apply {
                                    text = getTotalUpSpeed(noSpace, noSecond)
                                    setTextSize(
                                        TypedValue.COMPLEX_UNIT_DIP, getDoubleSize.toFloat()
                                    )
                                    if (setInterval != -1) layoutParams =
                                        LayoutParams(layoutParams).apply {
                                            bottomMargin = bMargin + (setInterval.dp / 2)
                                        }
                                }
                                mSpeedUnit.apply {
                                    text = getTotalDownloadSpeed(noSpace, noSecond)
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

        //获取总的上行速度
        @SuppressLint("NewApi")
        private fun getTotalUpSpeed(noSpace: Boolean, noSecond: Boolean): String {
            //换算后的上行速度
            var totalUpSpeed: String

            /** 当前总的上行流量 */
            var allTotalUpBytes = TrafficStats.getTotalTxBytes()
            if (SDK >= A12) allTotalUpBytes -= TrafficStats.getTxBytes(LOOPBACK_IFACE)
            if (lastTotalUpBytes == 0L) lastTotalUpBytes = allTotalUpBytes
            val currentTotalUp = allTotalUpBytes - lastTotalUpBytes

            /** 当前总的间隔时间 */
            val currentTotalUpTime = System.currentTimeMillis()
            if (lastTotalUpTime == 0L) lastTotalUpTime = currentTotalUpTime
            val timeIntervals = currentTotalUpTime - lastTotalUpTime

            //计算上传速度
            val bytes = (currentTotalUp / (timeIntervals / 1000.0)).toFloat()
            if (bytes.isInfinite() || bytes.isNaN() || bytes < 0) {
                totalUpSpeed = "0 B/s"
            } else {
                totalUpSpeed = getTotalFormatSpeed(bytes)

                //保存当前的流量总和和上次的时间戳
                lastTotalUpBytes = allTotalUpBytes
                lastTotalUpTime = currentTotalUpTime
            }

            //输出最终速度字符串
            if (noSpace) totalUpSpeed = totalUpSpeed.replace(" ", "")
            if (noSecond) totalUpSpeed = totalUpSpeed.replace("/s", "")
            return totalUpSpeed
        }

        //获取总的下行速度
        @SuppressLint("NewApi")
        private fun getTotalDownloadSpeed(noSpace: Boolean, noSecond: Boolean): String {
            //换算后的下行速度
            var totalDownSpeed: String

            /** 当前总的下行流量 */
            var allTotalDownBytes = TrafficStats.getTotalRxBytes()
            if (SDK >= A12) allTotalDownBytes -= TrafficStats.getRxBytes(LOOPBACK_IFACE)
            if (lastTotalDownBytes == 0L) lastTotalDownBytes = allTotalDownBytes
            val currentTotalDown = allTotalDownBytes - lastTotalDownBytes

            /** 当前总的间隔时间 */
            val currentTotalDownTime = System.currentTimeMillis()
            if (lastTotalDownTime == 0L) lastTotalDownTime = currentTotalDownTime
            val timeIntervals = currentTotalDownTime - lastTotalDownTime

            //计算下行速度
            val bytes = (currentTotalDown / (timeIntervals / 1000.0)).toFloat()
            if (bytes.isInfinite() || bytes.isNaN() || bytes < 0) {
                totalDownSpeed = "0 B/s"
            } else {
                totalDownSpeed = getTotalFormatSpeed(bytes)

                //保存当前的流量总和和上次的时间戳
                lastTotalDownBytes = allTotalDownBytes
                lastTotalDownTime = currentTotalDownTime
            }

            //输出最终速度字符串
            if (noSpace) totalDownSpeed = totalDownSpeed.replace(" ", "")
            if (noSecond) totalDownSpeed = totalDownSpeed.replace("/s", "")
            return totalDownSpeed
        }

        private fun getTotalFormatSpeed(bytes: Float): String {
            return if (bytes >= (1024 * 1024)) {
                "%.1f MB/s".format(bytes / (1024 * 1024))
            } else if (bytes >= 1024) {
                "%.1f KB/s".format(bytes / 1024)
            } else {
                "%.1f B/s".format(bytes)
            }
        }

    }

}