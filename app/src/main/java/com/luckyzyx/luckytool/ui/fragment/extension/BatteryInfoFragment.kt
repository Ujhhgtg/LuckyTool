package com.luckyzyx.luckytool.ui.fragment.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.luckyzyx.luckytool.databinding.FragmentBatteryInfoBinding
import com.luckyzyx.luckytool.utils.LogUtils
import org.lsposed.lsparanoid.Obfuscate
import java.util.Properties

@Obfuscate
class BatteryInfoFragment : Fragment() {

    private lateinit var binding: FragmentBatteryInfoBinding

    companion object {
        val TAG = "BatteryInfoFragment"

        //battery
        private var status: String = ""
        private var statusValue: Int = 0
        private var plugged: String = ""
        private var level: Int = 0
        private var level_sub: Int = 0
        private var temperature: Double = 0.0
        private var temperature_noplug: Double = 0.0
        private var voltage: Double = 0.0
        private var voltage2: Double = 0.0
        private var electricCurrent: Int = 0
        private var wirelessVol: Double = 0.0
        private var wirelessCur: Int = 0

        private var isCharging: Boolean = false
        private var isWireless: Boolean = false

        //oplus battery
        private var chargerVoltage: Int = 0
        private var chargerTechnology: Int = 0
        private var chargeWattage: Int = 0
        private var ppsMode: Int = 0
        private var chargerWattageCpa: Int = -1
        private var usbFastChgType: Int = 0

        private var isSeriesDual = false
        private var isParallelDual = false
        private var chargerType = ""

        private var oplusCharger: Any? = null
        private lateinit var chargeInfo: Properties
        private var isMTKPlatform: Boolean? = null

        private var curHealth: Int? = null
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            LogUtils.d(TAG, "onReceive", "${intent.action}", true)
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    init()

                }

                "android.intent.action.ADDITIONAL_BATTERY_CHANGED" -> {
                    init()

                }
            }
        }
    }

    fun init() {
//        val instance = OplusBatteryServiceFeature.getInstance()
//        LogUtils.d(TAG, "init", "${instance != null}", true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBatteryInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction("android.intent.action.ADDITIONAL_BATTERY_CHANGED")
        }
        val status = ContextCompat.registerReceiver(
            requireActivity(),
            receiver,
            intentFilter,
            null,
            null,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        LogUtils.d(TAG, "registerReceiver", "${status != null}", true)

    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(receiver)
        LogUtils.d(TAG, "unregisterReceiver", "", true)

    }
}