package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
class HookPackageInstallerFeature(val clazz: Class<*>?) : YukiBaseHooker() {
    override fun onHook() {
        val isAOSP = false//(ModulePrefs).getBoolean("replase_aosp_installer", false)
        val isAds = prefs(ModulePrefs).getBoolean("remove_install_ads", false)

        //Source FeatureOption
        clazz?.apply {
            method { name = "init";paramCount = 1 }.hook {
                after {
                    if (isAds) field { name = "sIsBusinessCustomProduct" }.get().setFalse()
                }
            }
            method { name = "setIsClosedSuperFirewall";paramCount = 1 }.hook {
                after {
                    if (isAOSP) field { name = "sIsClosedSuperFirewall" }.get().setTrue()
                }
            }
        }
    }
}