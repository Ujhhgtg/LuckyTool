package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveInstallAds : YukiBaseHooker() {
    override fun onHook() {
        var ins: Any? = null
        //Source InstallAppProgress
        "com.android.packageinstaller.oplus.InstallAppProgress".toClass().resolve().apply {
            firstMethod { name = "initView" }.hook {
                after {
                    ins = instance
                    ins.removeViews()
                }
            }
        }
        //Source InstallAppProgress
        "com.android.packageinstaller.oplus.InstallAppProgress$1".toClass().resolve().apply {
            firstMethod { name = "handleMessage" }.hook {
                after {
                    ins?.removeViews()
                }
            }
        }
    }

    private fun Any.removeViews() {
        asResolver<Any>().firstField { name = "mSuggestLayoutAScrollView" }.get<View>()?.isVisible = false
        asResolver<Any>().firstField { name = "mSuggestLayoutB" }.get<View>()?.isVisible = false
    }
}