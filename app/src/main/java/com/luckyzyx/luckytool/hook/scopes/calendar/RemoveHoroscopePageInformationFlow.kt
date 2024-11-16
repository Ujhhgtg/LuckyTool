package com.luckyzyx.luckytool.hook.scopes.calendar

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveHoroscopePageInformationFlow : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source HoroscopeFragment -> H5InterfaceHelper getHoroscopeUrl
        "com.android.calendar.module.subscription.horoscope.HoroscopeFragment".toClass()
            .apply {
                method { name = "onViewCreated";paramCount = 2 }.hook {
                    after {
                        val viewGroup = args().first().cast<ViewGroup>() ?: return@after
                        val res = viewGroup.resources
                        viewGroup.findViewById<View>(
                            res.getIdentifier(
                                "web_title_layout", "id",
                                this@RemoveHoroscopePageInformationFlow.packageName
                            )
                        )?.let { (it.parent as ViewGroup).removeView(it) }
                        viewGroup.findViewById<WebView>(
                            res.getIdentifier(
                                "webView", "id",
                                this@RemoveHoroscopePageInformationFlow.packageName
                            )
                        )?.let { (it.parent as ViewGroup).removeView(it) }
                        viewGroup.findViewById<View>(
                            res.getIdentifier(
                                "fb_to_top", "id",
                                this@RemoveHoroscopePageInformationFlow.packageName
                            )
                        )?.let { (it.parent as ViewGroup).removeView(it) }
                        viewGroup.findViewById<View>(
                            res.getIdentifier(
                                "tv_source", "id",
                                this@RemoveHoroscopePageInformationFlow.packageName
                            )
                        )?.isVisible = true
                    }
                }
            }
    }
}