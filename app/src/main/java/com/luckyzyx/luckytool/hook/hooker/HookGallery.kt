package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.gallery.HookConfigAbility
import com.luckyzyx.luckytool.hook.scope.gallery.HookFunctionManager
import com.luckyzyx.luckytool.hook.scope.gallery.HookSystemStorage
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppSet

object HookGallery : YukiBaseHooker() {
    override fun onHook() {
        val appSet = getAppSet(ModulePrefs, packageName)
        if (appSet[1].toIntOrNull()?.let { it < 13000000 } == true) return

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //HookOtherSystemStorage
            loadHooker(HookSystemStorage(dexKitBridge))
            //HookConfigAbility
            loadHooker(HookConfigAbility(dexKitBridge))
            //HookFunctionManager
            loadHooker(HookFunctionManager(dexKitBridge))
        }
    }
}