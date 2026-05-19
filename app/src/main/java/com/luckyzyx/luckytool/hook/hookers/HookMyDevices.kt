package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.mydevices.ForceEnableFeiniuCloudNasOption
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookMyDevices : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //强制启用飞牛云NAS选项
            if (prefs(ModulePrefs).getBoolean("force_enable_feiniu_cloud_nas_option", false)) {
                loadHooker(ForceEnableFeiniuCloudNasOption(dexKitBridge))
            }
        }

    }
}