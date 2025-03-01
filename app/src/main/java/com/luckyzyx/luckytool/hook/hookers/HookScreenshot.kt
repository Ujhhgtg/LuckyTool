package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.screenshot.CustomizeLongScreenshotMaxCapturedPages
import com.luckyzyx.luckytool.hook.scopes.screenshot.EnablePNGSaveFormat
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppVerInfo

@Obfuscate
object HookScreenshot : YukiBaseHooker() {
    override fun onHook() {
        val appVer = prefs(ModulePrefs).getAppVerInfo(packageName)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //移除长截图页数限制
            if (prefs(ModulePrefs).getBoolean("remove_page_limit_for_long_screenshots", false)) {
                val exist = appVer?.versionCode?.let { it > 130005000 } ?: false
                if (exist) loadHooker(CustomizeLongScreenshotMaxCapturedPages(dexKitBridge))
            }
            //启用PNG保存格式
            if (prefs(ModulePrefs).getBoolean("enable_png_save_format", false)) {
                loadHooker(EnablePNGSaveFormat(dexKitBridge))
            }
        }
    }
}