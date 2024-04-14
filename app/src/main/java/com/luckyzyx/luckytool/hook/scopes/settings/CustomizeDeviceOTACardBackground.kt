package com.luckyzyx.luckytool.hook.scopes.settings

import android.graphics.BitmapFactory
import android.view.View
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp

object CustomizeDeviceOTACardBackground : YukiBaseHooker() {
    override fun onHook() {
        val backgroundPath =
            prefs(ModulePrefs).getString("customize_device_ota_card_background_path", "")

        //Source AboutDeviceOtaUpdatePreference
        "com.oplus.settings.widget.preference.AboutDeviceOtaUpdatePreference".toClass().apply {
            method { name = "onBindViewHolder" }.hook {
                after {
                    val mLogoView = field { name = "mLogoView" }.get(instance).cast<View>()
                        ?: return@after
                    val context = mLogoView.context
                    (mLogoView.parent as RelativeLayout).apply {
                        val bitmap = BitmapFactory.decodeFile(backgroundPath) ?: return@after
                        val drawable =
                            RoundedBitmapDrawableFactory.create(context.resources, bitmap).apply {
                                cornerRadius = 12F.dp
                            }
                        background = drawable
                    }
                }
            }
        }
    }
}