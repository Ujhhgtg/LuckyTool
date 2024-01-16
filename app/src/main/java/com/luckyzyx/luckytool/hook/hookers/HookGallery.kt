package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.gallery.HookFunctionManager
import com.luckyzyx.luckytool.hook.scopes.gallery.HookSystemStorage
import com.luckyzyx.luckytool.hook.scopes.gallery.RemoveGalleryWaterMarkWordLimit
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookGallery : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) return

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //HookOtherSystemStorage
            loadHooker(HookSystemStorage(dexKitBridge))
            //HookFunctionManager
            loadHooker(HookFunctionManager(dexKitBridge))

            //移除自定义水印字数限制
            if (prefs(ModulePrefs).getBoolean("remove_gallery_watermark_word_limit", false)) {
                if (SDK >= A13) loadHooker(RemoveGalleryWaterMarkWordLimit(dexKitBridge))
            }
        }
    }
}