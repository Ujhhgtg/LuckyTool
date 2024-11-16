package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.externalstorage.RemoveStorageLimit
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookExternalStorage : YukiBaseHooker() {
    override fun onHook() {
        //移除存储限制
        if (prefs(ModulePrefs).getBoolean("remove_storage_limit", false)) {
            loadHooker(RemoveStorageLimit)
        }
    }
}