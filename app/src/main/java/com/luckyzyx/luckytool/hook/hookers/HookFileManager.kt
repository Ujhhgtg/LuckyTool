package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.filemanager.RemoveWordLimitForSavingFiles
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookFileManager : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->

            //移除文件保存字数限制
            if (prefs(ModulePrefs).getBoolean("remove_word_limit_for_saving_files", false)) {
                if (osCode >= 37) loadHooker(RemoveWordLimitForSavingFiles(dexKitBridge))
            }

        }

    }
}