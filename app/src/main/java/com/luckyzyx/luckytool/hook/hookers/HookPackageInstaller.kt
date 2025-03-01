package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AllowReplaceInstall
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AutoClickInstallButton
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.AutoClickUnInstallButton
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.FixInstallButtonDisplayException
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.HookPackageInstallerFeature
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.RemoveInstallAds
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.ShowMoreApkPackageInformation
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.SkipApkScan
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppVerInfo

@Obfuscate
object HookPackageInstaller : YukiBaseHooker() {
    override fun onHook() {
        val appVer = prefs(ModulePrefs).getAppVerInfo(packageName)

        //HookFeatureOption
        val featureOption = "com.android.packageinstaller.oplus.common.FeatureOption"
            .toClassOrNull() ?: return
        loadHooker(HookPackageInstallerFeature(featureOption))

        //跳过安装扫描
        if (prefs(ModulePrefs).getBoolean("skip_apk_scan", false)) {
            loadHooker(SkipApkScan(appVer?.versionCommit))
        }
        //修复App安装页面底部按钮异常
        if (prefs(ModulePrefs).getBoolean("fix_install_button_display_exception", false)) {
            loadHooker(FixInstallButtonDisplayException)
        }
        //低/相同版本警告
        if (prefs(ModulePrefs).getBoolean("allow_downgrade_install", false)) {
            loadHooker(AllowReplaceInstall)
        }
        //移除安装完成广告
        if (prefs(ModulePrefs).getBoolean("remove_install_ads", false)) {
            loadHooker(RemoveInstallAds)
        }
        //自动点击安装按钮
        if (prefs(ModulePrefs).getBoolean("auto_click_install_button", false)) {
            loadHooker(AutoClickInstallButton)
        }
        //自动点击卸载按钮
        if (prefs(ModulePrefs).getBoolean("auto_click_uninstall_button", false)) {
            loadHooker(AutoClickUnInstallButton)
        }
        //显示更多Apk包信息
        if (prefs(ModulePrefs).getBoolean("show_more_apk_package_information", false)) {
            loadHooker(ShowMoreApkPackageInformation)
        }
    }
}