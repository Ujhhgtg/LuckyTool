package com.luckyzyx.luckytool.hook.scopes.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.children
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object CustomizeDeviceOTACardBackground : YukiBaseHooker() {

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        val osCode = getOSVersionCode
        val backgroundPath =
            prefs(ModulePrefs).getString("customize_device_ota_card_background_path", "")
        val hideText = prefs(ModulePrefs).getBoolean("hide_ota_card_top_text", false)
        val applySharePage =
            prefs(ModulePrefs).getBoolean("apply_device_parameter_sharing_page", false)

        //Source AboutDeviceOtaUpdatePreference
        "com.oplus.settings.widget.preference.AboutDeviceOtaUpdatePreference".toClass().apply {
            method { name = "onBindViewHolder" }.hook {
                after {
                    val holder = args().first().any() ?: return@after
                    val itemView = holder.current().field { name = "itemView";superClass() }
                        .cast<View>() ?: return@after
                    if (itemView is RelativeLayout) {
                        val topId = itemView.resources.getIdentifier(
                            "about_device_top_bg", "id",
                            this@CustomizeDeviceOTACardBackground.packageName
                        )
                        itemView.findViewById<View>(topId)?.let {
                            itemView.removeView(it)
                        }
                        val bitmap = BitmapFactory.decodeFile(backgroundPath) ?: return@after
                        val drawableFactory = RoundedBitmapDrawableFactory.create(
                            itemView.resources, bitmap
                        )
                        drawableFactory.cornerRadius = 12F.dp
                        itemView.background = drawableFactory

                        if (hideText) itemView.children.forEach {
                            it.isVisible = false
                        }
                    }
                }
            }
        }

        if (osCode < 34 || !applySharePage) return

        //Source ShareAboutPhoneActivity parent_relativeLayout
        "com.oplus.settings.feature.deviceinfo.aboutphone.ShareAboutPhoneActivity".toClass().apply {
            method { name = "updateOsVersion" }.hook {
                after {
                    val activity = instance<Activity>()
                    val viewId = activity.resources.getIdentifier(
                        "parent_relativeLayout", "id",
                        this@CustomizeDeviceOTACardBackground.packageName
                    )
                    val relativeLayout = activity.findViewById<RelativeLayout>(viewId)
                        ?: return@after
                    val topId = activity.resources.getIdentifier(
                        "about_device_top_bg", "id",
                        this@CustomizeDeviceOTACardBackground.packageName
                    )
                    activity.findViewById<View>(topId)?.let {
                        relativeLayout.removeView(it)
                    }
                    val bitmap = BitmapFactory.decodeFile(backgroundPath) ?: return@after
                    val drawableFactory = RoundedBitmapDrawableFactory.create(
                        relativeLayout.resources, bitmap
                    )
                    drawableFactory.cornerRadius = 12F.dp
                    relativeLayout.background = drawableFactory
                }
            }
        }
    }
}