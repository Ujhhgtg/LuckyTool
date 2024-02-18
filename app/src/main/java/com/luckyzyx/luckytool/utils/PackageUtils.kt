@file:Suppress("unused", "NewApi")

package com.luckyzyx.luckytool.utils

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.InstallSourceInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.ResolveInfoFlags
import android.content.pm.ResolveInfo
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.data.AppInfo
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
@Obfuscate
class PackageUtils(private val packageManager: PackageManager) {
    fun getPackageInfo(packName: String, flag: Int): PackageInfo? {
        return try {
            if (SDK < A13) packageManager.getPackageInfo(packName, flag)
            else packageManager.getPackageInfo(
                packName, PackageManager.PackageInfoFlags.of(flag.toLong())
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getNameForUid(uid: Int): String? {
        return packageManager.getNameForUid(uid)
    }

    fun getInstallSourceInfo(packName: String): InstallSourceInfo? {
        return try {
            packageManager.getInstallSourceInfo(packName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getPackageUid(packName: String, flag: Int): Int? {
        return try {
            if (SDK < A13) packageManager.getPackageUid(packName, flag)
            else packageManager.getPackageUid(
                packName, PackageManager.PackageInfoFlags.of(flag.toLong())
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getApplicationInfo(packName: String, flag: Int): ApplicationInfo? {
        return try {
            if (SDK < A13) packageManager.getApplicationInfo(packName, flag)
            else packageManager.getApplicationInfo(
                packName, PackageManager.ApplicationInfoFlags.of(flag.toLong())
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getInstalledPackages(flag: Int): MutableList<PackageInfo> {
        if (SDK < A13) return packageManager.getInstalledPackages(flag)
        return packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flag.toLong()))
    }

    fun getInstalledApplications(flag: Int): MutableList<ApplicationInfo> {
        return if (SDK < A13) packageManager.getInstalledApplications(flag)
        else packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(flag.toLong()))
    }

    fun resolveActivity(intent: Intent, flag: Int): ResolveInfo? {
        return if (SDK < A13) packageManager.resolveActivity(intent, flag)
        else packageManager.resolveActivity(intent, ResolveInfoFlags.of(flag.toLong()))
    }

    fun getApplicationEnabledSetting(packName: String): Boolean {
        return packageManager.getApplicationEnabledSetting(packName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    }

    fun getInstalledAppInfos(flag: Int, allowSystem: Boolean = false): ArrayList<AppInfo> {
        val appInfoList = ArrayList<AppInfo>()
        getInstalledPackages(flag).forEachIndexed { _, info ->
            try {
                val applicationInfo = info.applicationInfo ?: return@forEachIndexed
                if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1 && !allowSystem) return@forEachIndexed
                val name = applicationInfo.loadLabel(packageManager)
                val icon = applicationInfo.loadIcon(packageManager)
                val size = FileUtils.getFileSize(File(applicationInfo.sourceDir))
                val versionName = info.versionName
                val versionCode = info.longVersionCode
                val installTime = info.firstInstallTime
                val lastInstallTime = info.lastUpdateTime
                val target = applicationInfo.targetSdkVersion
                val isEnable = getApplicationEnabledSetting(info.packageName)
                appInfoList.add(
                    AppInfo(
                        name.toString(), info.packageName, icon, size, versionName, versionCode,
                        installTime, lastInstallTime, target, isEnable
                    )
                )
            } catch (e: Exception) {
                LogUtils.e("getInstalledAppInfos", info.packageName, info.toString())
            }
        }
        return appInfoList
    }
}

