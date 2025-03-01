package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalSystemProperties
import com.luckyzyx.luckytool.hook.scopes.ota.RemoveOTALocalUpdateVerity
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookOplusOta : YukiBaseHooker() {
    override fun onHook() {

        loadHooker(HookGlobalSystemProperties)

        //local_update_failed_not_match 安装包不匹配
        //local_update_failed_read_exception 读取文件错误
        //local_update_failed_not_exist 文件不存在
        //local_update_low_memory 存储空间不足不能安装

        //local_update_verify_failed 验证失败
        //unzip_file_failed 解压失败

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //移除OTA本地更新校验
            if (prefs(ModulePrefs).getBoolean("remove_ota_local_update_verity", false)) {
                loadHooker(RemoveOTALocalUpdateVerity(dexKitBridge))
            }
        }

    }
}