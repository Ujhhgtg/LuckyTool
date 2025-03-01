package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookAlertSlider : YukiBaseHooker() {
    override fun onHook() {
        //Source AlertSliderAudioPolicy
        "com.android.server.audio.AlertSliderAudioPolicy".toClass().apply {
            method { name = "setUp" }.hook {
                before {
                    YLog.debug("${method.name} is call")
                    resultNull()
                }
            }
            method { name = "setMiddle" }.hook {
                before {
                    YLog.debug("${method.name} is call")
                    resultNull()
                }
            }
            method { name = "setDown" }.hook {
                before {
                    YLog.debug("${method.name} is call")
                    resultNull()
                }
            }
        }
    }
}