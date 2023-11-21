package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.gallery.HookFunctionManager
import com.luckyzyx.luckytool.hook.scope.gallery.HookSystemStorage
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.SDK

object HookGallery : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) return

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //HookOtherSystemStorage
            loadHooker(HookSystemStorage(dexKitBridge))
            //HookFunctionManager
            loadHooker(HookFunctionManager(dexKitBridge))
        }
    }
}