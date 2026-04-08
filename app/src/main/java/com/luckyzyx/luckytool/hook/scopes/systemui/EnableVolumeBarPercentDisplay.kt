package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableVolumeBarPercentDisplay : YukiBaseHooker() {
    override fun onHook() {
//        val color = prefs(ModulePrefs).getString("","")

        //Source OplusVolumeSeekBar
        "com.oplus.systemui.volume.OplusVolumeSeekBar".toClass().resolve().apply {
            firstMethod { name = "drawActiveTrack" }.hook {
                before {
                    val getProgress = firstMethod { name = "getProgress"; superclass() }
                        .of(instance).invoke<Int>() ?: return@before
                    val getMax = firstMethod { name = "getMax"; superclass() }.of(instance)
                        .invoke<Int>() ?: return@before
                    val percent = ((getProgress.toDouble() / getMax.toDouble()) * 100).toInt()
                    firstField { name = "mShowText"; superclass() }.of(instance).set(true)
                    firstMethod { name = "setText"; superclass() }.of(instance).invoke("$percent%")
                }
            }
        }
    }
}