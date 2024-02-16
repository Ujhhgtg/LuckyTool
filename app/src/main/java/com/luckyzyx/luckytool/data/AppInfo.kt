package com.luckyzyx.luckytool.data

import android.graphics.drawable.Drawable
import com.joom.paranoid.Obfuscate
import java.io.Serializable

@Obfuscate
@Suppress("MemberVisibilityCanBePrivate")
class AppInfo : Serializable {

    val name: String
    val packName: String
    val icon: Drawable
    val size: Long
    val versionName: String
    val versionCode: Long
    val installTime: Long
    val lastInstallTime: Long
    val target: Int
    val isEnable: Boolean

    constructor(
        name: String,
        packName: String,
        icon: Drawable,
        size: Long,
        versionName: String,
        versionCode: Long,
        installTime: Long,
        lastInstallTime: Long,
        target: Int,
        isEnable: Boolean,

        ) {
        this.name = name
        this.packName = packName
        this.icon = icon
        this.size = size
        this.installTime = installTime
        this.lastInstallTime = lastInstallTime
        this.target = target
        this.isEnable = isEnable
        this.versionName = versionName
        this.versionCode = versionCode
    }
}