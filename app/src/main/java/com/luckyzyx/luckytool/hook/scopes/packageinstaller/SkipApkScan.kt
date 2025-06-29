package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class SkipApkScan(private val commit: String?) : YukiBaseHooker() {

    @Suppress("LocalVariableName")
    override fun onHook() {
        val OPIA = "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity"
        val ADRU = "com.android.packageinstaller.oplus.utils.AppDetailRedirectionUtils"
        val isNew = ADRU.toClass().resolve().firstMethodOrNull { name = "shouldStartAppDetail" }
        val member: Array<String> =
            if (isNew != null) {
                arrayOf(ADRU, "shouldStartAppDetail", "checkToScanRisk", "initiateInstall")
            } else when (commit) {
                "7bc7db7", "e1a2c58" -> arrayOf(OPIA, "L", "C", "K")
                "75fe984", "532ffef" -> arrayOf(OPIA, "L", "D", "i")
                "38477f0" -> arrayOf(OPIA, "M", "D", "k")
                "a222497" -> arrayOf(OPIA, "M", "E", "j")
                else -> arrayOf(OPIA, "isStartAppDetail", "checkToScanRisk", "initiateInstall")
            }
        //Source OPlusPackageInstallerActivity ? AppDetailRedirectionUtils
        //Search SP_KEY_COUNT_CANCELED_BY_APP_DETAIL / count_canceled_by_app_detail
        member[0].toClass().resolve().apply {
            method {
                name = member[1]
                if (member[0] == OPIA) returnType = Boolean::class
                if (member[0] == ADRU) returnType = Int::class
            }.hookAll {
                if (member[0] == OPIA) replaceToFalse()
                if (member[0] == ADRU) replaceTo(9)
            }
        }
        //Source OPlusPackageInstallerActivity
        //Search button_type / install_old_version_button
        OPIA.toClass().resolve().apply {
            firstMethod { name = member[2] }.hook {
                before {
                    firstMethod { name = member[3] }.of(instance).invoke()
                    resultNull()
                }
            }
        }
    }
}
