package com.luckyzyx.luckytool.hook.scopes.battery

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.calcLocalHealth
import com.luckyzyx.luckytool.utils.filterNumber
import com.luckyzyx.luckytool.utils.safeOf

object DisplayModuleCalculatesBatteryHealthData : YukiBaseHooker() {
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onHook() {
        val customCalcData =
            prefs(ModulePrefs).getString("customize_battery_health_data_percentage", "None")
        val showCalcData =
            prefs(ModulePrefs).getBoolean("display_module_calculates_battery_health_data", false)

        //Source BatteryHealthDataPreference
        "com.oplus.powermanager.fuelgaue.BatteryHealthDataPreference".toClass().apply {
            method { param(ViewClass) }.hook {
                after {
                    val view = args().first().cast<View>() ?: return@after
                    val context = view.context
                    context.injectModuleAppResources()
                    val contentView = view.findViewById<TextView>(
                        view.resources.getIdentifier(
                            "max_capacity_content",
                            "id", this@DisplayModuleCalculatesBatteryHealthData.packageName
                        )
                    ) ?: return@after
                    val dataView = field { type = TextViewClass }.get(instance).cast<TextView>()
                        ?: return@after
                    if (customCalcData.filterNumber.isNotEmpty()) {
                        dataView.text = "${customCalcData.filterNumber}%"
                    }
                    if (showCalcData) {
                        val health = context.calcLocalHealth(true)
                        val tips = safeOf(" Calc") {
                            context.getString(R.string.display_module_calculates_battery_health_data_tips)
                        }
                        contentView.apply {
                            layoutParams?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                            gravity = Gravity.START
                            text = "$text\n\nLuckyTool$tips"
                        }
                        dataView.apply {
                            layoutParams?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                            gravity = Gravity.END
                            text = "$text\n\n${health}%"
                        }
                    }
                }
            }
        }
    }
}