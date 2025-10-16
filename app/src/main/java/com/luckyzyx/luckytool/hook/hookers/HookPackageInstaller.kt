package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AllowReplaceInstall
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AutoClickInstallButton
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AutoClickUnInstallButton
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.FixInstallButtonDisplayException
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.RemoveInstallAds
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.ShowMoreApkPackageInformation
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.SkipDetailApkScan
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookPackageInstaller : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //Source FeatureOption
            dexKitBridge.findClass {
                matcher {
                    usingStrings(
                        "oppo.business.custom",
                        "com.oplus.packageinstaller.skip_appdetail",
                        "com.oplus.unknown_source_app_install"
                    )
                }
            }.singleOrNull() ?: return@create

            //跳过安装扫描
            if (prefs(ModulePrefs).getBoolean("skip_apk_scan", false)) {
                loadHooker(SkipDetailApkScan(dexKitBridge))
            }
            //修复App安装页面底部按钮异常
            if (prefs(ModulePrefs).getBoolean("fix_install_button_display_exception", false)) {
                loadHooker(FixInstallButtonDisplayException)
            }
            //低/相同版本警告
            if (prefs(ModulePrefs).getBoolean("allow_downgrade_install", false)) {
                loadHooker(AllowReplaceInstall(dexKitBridge))
            }
            //移除安装完成广告
            if (prefs(ModulePrefs).getBoolean("remove_install_ads", false)) {
                loadHooker(RemoveInstallAds(dexKitBridge))
            }
            //自动点击安装按钮
            if (prefs(ModulePrefs).getBoolean("auto_click_install_button", false)) {
                loadHooker(AutoClickInstallButton)
            }
            //自动点击卸载按钮
            if (prefs(ModulePrefs).getBoolean("auto_click_uninstall_button", false)) {
                loadHooker(AutoClickUnInstallButton)
            }
            return@create
            //显示更多Apk包信息
            if (prefs(ModulePrefs).getBoolean("show_more_apk_package_information", false)) {
                loadHooker(ShowMoreApkPackageInformation(dexKitBridge))
            }
        }
    }
}