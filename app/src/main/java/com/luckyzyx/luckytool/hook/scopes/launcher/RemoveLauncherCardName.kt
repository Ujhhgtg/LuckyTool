package com.luckyzyx.luckytool.hook.scopes.launcher

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
            //Source CardNameHelper
            "com.android.launcher3.card.utils.CardNameHelper".toClass().resolve().apply {
                firstMethod { name = "initCardName" }.hook {
                    intercept()
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