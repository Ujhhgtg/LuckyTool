package com.luckyzyx.luckytool.hook.scopes.pictorial

import android.widget.LinearLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveVideoSaveWaterMark : YukiBaseHooker() {
    override fun onHook() {
        //Source VideoWaterMarkView -> view_video_water_mark
        "com.heytap.pictorial.data.VideoWaterMarkView".toClass().apply {
            constructor().hook {
                after {
                    instance<LinearLayout>().removeAllViews()
                }
            }
        }
    }
}