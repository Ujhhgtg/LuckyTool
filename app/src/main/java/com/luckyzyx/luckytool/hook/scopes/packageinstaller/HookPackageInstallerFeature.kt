package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookPackageInstallerFeature(val clazz: Class<*>?) : YukiBaseHooker() {
    override fun onHook() {
        val isAOSP = false//(ModulePrefs).getBoolean("replase_aosp_installer", false)
        val isAds = prefs(ModulePrefs).getBoolean("remove_install_ads", false)

        //Source FeatureOption
        clazz?.resolve()?.apply {
            firstMethod { name = "init";parameterCount = 1 }.hook {
                after {
                    if (isAds) firstField { name = "sIsBusinessCustomProduct" }.set(false)
                }
            }
            firstMethod { name = "setIsClosedSuperFirewall";parameterCount = 1 }.hook {
                after {
                    if (isAOSP) firstField { name = "sIsClosedSuperFirewall" }.set(true)
                }
            }
        }
    }
}