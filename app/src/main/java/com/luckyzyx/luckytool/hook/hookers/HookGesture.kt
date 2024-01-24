package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.gesture.CustomAonGestureScrollPageWhitelist
import com.luckyzyx.luckytool.utils.DexkitUtils

object HookGesture : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //自定义滑动页面白名单
            //自定义视频手势白名单
            loadHooker(CustomAonGestureScrollPageWhitelist(dexKitBridge))
        }
    }
}