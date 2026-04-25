package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveLauncherCardName : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 30) loadHooker(LauncherCardName)
        else loadHooker(LauncherCardNameV13)
    }

    @Obfuscate
    object LauncherCardName : YukiBaseHooker() {
        override fun onHook() {
            //Source LauncherCardView
            "com.android.launcher3.card.LauncherCardView".toClass().resolve().apply {
                firstMethod { name = "getLauncherCardName" }.hook {
                    after {
                        result<TextView>()?.text = null
                    }
                }
            }
        }
    }

    @Obfuscate
    object LauncherCardNameV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source TitleCardView
            "com.android.launcher3.card.TitleCardView".toClass().resolve().apply {
                firstMethod { name = "initCardName" }.hook {
                    intercept()
                }
            }

            //Source USCardContainerView
            "com.android.launcher3.card.uscard.USCardContainerView".toClass().resolve().apply {
                firstMethod { name = "initCardName" }.hook {
                    intercept()
                }
            }
        }
    }
}