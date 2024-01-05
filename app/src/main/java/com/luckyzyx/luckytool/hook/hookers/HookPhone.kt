package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.phone.ForceDisplaySomeSwitch
import com.luckyzyx.luckytool.utils.DexkitUtils

object HookPhone : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)
        loadHooker(HookGlobalSystemProperties)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //强制显示部分开关
            loadHooker(ForceDisplaySomeSwitch(dexKitBridge))
        }
    }
}