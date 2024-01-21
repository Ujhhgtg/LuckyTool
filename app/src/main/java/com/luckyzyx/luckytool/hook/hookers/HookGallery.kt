package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.gallery.HookFunctionManager
import com.luckyzyx.luckytool.hook.scopes.gallery.HookSystemStorage
import com.luckyzyx.luckytool.hook.scopes.gallery.RemoveGalleryWaterMarkWordLimit
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object HookGallery : YukiBaseHooker() {
    override fun onHook() {
        if (getOSVersionCode < 27) return

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //HookOtherSystemStorage
            loadHooker(HookSystemStorage(dexKitBridge))
            //HookFunctionManager
            loadHooker(HookFunctionManager(dexKitBridge))

            //移除自定义水印字数限制
            if (prefs(ModulePrefs).getBoolean("remove_gallery_watermark_word_limit", false)) {
                loadHooker(RemoveGalleryWaterMarkWordLimit(dexKitBridge))
            }
        }
    }
}