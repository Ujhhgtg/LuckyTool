package com.luckyzyx.commonutils.data

import android.graphics.drawable.Drawable
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Suppress("MemberVisibilityCanBePrivate")
@Obfuscate
class AppInfo : Serializable {

    val name: String
    val packageName: String
    val icon: Drawable?
    val size: Long
    val versionName: String
    val versionCode: Long
    val installTime: Long
    val lastInstallTime: Long
    val target: Int
    var isSystem: Boolean
    var isEnable: Boolean

    constructor() {
        this.name = ""
        this.packageName = ""
        this.icon = null
        this.size = 0L
        this.versionName = ""
        this.versionCode = 0L
        this.installTime = 0L
        this.lastInstallTime = 0L
        this.target = 0
        this.isSystem = false
        this.isEnable = false
    }

    constructor(
        name: String,
        packageName: String,
        icon: Drawable,
        size: Long,
        versionName: String,
        versionCode: Long,
        installTime: Long,
        lastInstallTime: Long,
        target: Int,
        isSystem: Boolean = false,
        isEnable: Boolean = true,
    ) {
        this.name = name
        this.packageName = packageName
        this.icon = icon
        this.size = size
        this.installTime = installTime
        this.lastInstallTime = lastInstallTime
        this.target = target
        this.isEnable = isEnable
        this.versionName = versionName
        this.versionCode = versionCode
        this.isSystem = isSystem
        this.isEnable = isEnable
    }
}