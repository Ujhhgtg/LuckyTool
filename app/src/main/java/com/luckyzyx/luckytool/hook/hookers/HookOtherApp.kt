package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.otherapp.HookADM
import com.luckyzyx.luckytool.hook.scopes.otherapp.HookAlphaBackupPro
import com.luckyzyx.luckytool.hook.scopes.otherapp.HookKsWeb

object HookOtherApp : YukiBaseHooker() {
    override fun onHook() {
        //Alpha Backup Pro
        if (packageName == "com.ruet_cse_1503050.ragib.appbackup.pro") loadHooker(HookAlphaBackupPro)

        //KSWEB
        if (packageName == "ru.kslabs.ksweb") loadHooker(HookKsWeb)

        //ADM
        if (packageName == "com.dv.adm") loadHooker(HookADM)
    }
}
