package com.luckyzyx.luckytool.hook.hookers

import android.os.Build
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.camera.CustomModelWaterMark
import com.luckyzyx.luckytool.hook.scopes.camera.HookCameraConfig
import com.luckyzyx.luckytool.hook.scopes.camera.RemoveWatermarkWordLimit
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getAppSet

object HookCamera : YukiBaseHooker() {
    override fun onHook() {
        val appSet = prefs(ModulePrefs).getAppSet(packageName)
        if (appSet[2] == "null") return

        //HookCameraConfig
        if (SDK >= A13) loadHooker(HookCameraConfig)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //自定义机型水印
            val isRealme = Build.MODEL.startsWith("RM", true)
            if (SDK >= A13 && isRealme.not()) loadHooker(CustomModelWaterMark(dexKitBridge))

            //移除水印字数限制
            if (prefs(ModulePrefs).getBoolean("remove_watermark_word_limit", false)) {
                loadHooker(RemoveWatermarkWordLimit(dexKitBridge))
            }
        }
    }
}