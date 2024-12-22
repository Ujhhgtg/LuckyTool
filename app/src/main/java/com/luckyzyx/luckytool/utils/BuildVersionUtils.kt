@file:Suppress("unused")

package com.luckyzyx.luckytool.utils

import android.os.Build
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.hook.utils.OplusBuildUtlils
import com.oplus.os.OplusBuild

/**SDK_INT版本*/
val SDK get() = Build.VERSION.SDK_INT

/**Android11 30 R*/
val A11 get() = Build.VERSION_CODES.R

/**Android12 31 S*/
val A12 get() = Build.VERSION_CODES.S

/**Android13 33 TIRAMISU*/
val A13 get() = Build.VERSION_CODES.TIRAMISU

/**Android14 34 UPSIDE_DOWN_CAKE*/
val A14 get() = Build.VERSION_CODES.UPSIDE_DOWN_CAKE

/**Android15 35 XX*/
val A15 get() = Build.VERSION_CODES.VANILLA_ICE_CREAM

/**
 * 获取构建版本名/版本号
 * @return [String]
 */
val getVersionName get() = BuildConfig.VERSION_NAME
val getVersionCode get() = BuildConfig.VERSION_CODE

/**
 * 获取OS版本名
 * V12
 * V12.1
 * V12.2
 * V13.0
 * V13.1
 * V13.1.1
 * V13.2
 * V14.0
 * V14.0.1
 * V14.0.2
 * V14.1.0
 * V15.0.0
 * V15.0.1
 */
val getOSVersionName: String
    get() = safeOf("null") {
        OplusBuildUtlils(null).getOSVersions?.get(OplusBuild.getOplusOSVERSION() - 1)
            ?: OplusBuild.VERSIONS[OplusBuild.getOplusOSVERSION() - 1]
    }

/**
 * 获取指定OS版本名
 * @param osCode Int
 * @return String
 */
fun getOSVersionName(osCode: Int): String = safeOf("null") {
    OplusBuildUtlils(null).getOSVersions?.get(osCode - 1)
        ?: OplusBuild.VERSIONS[osCode - 1]
}

/**
 * 获取OS版本号
 * 23 -> (c12)
 * 24 -> (c12.1)
 * 25 -> (c12.2)
 * 26 -> (c13.0)
 * 27 -> (c13.1)
 * 28 -> (c13.1.1)
 * 29 -> (c13.2)
 * 30 -> (c14.0)
 * 31 -> (c14.0.1)
 * 32 -> (c14.0.2)
 * 33 -> (c14.1.0)
 * 34 -> (c15.0.0)
 * 35 -> (c15.0.1)
 */
val getOSVersionCode get() = safeOf(0) { OplusBuild.getOplusOSVERSION() }

/**
 * 获取指纹厂商
 * @return String
 */
val getFingerPrintBrand: String get() = safeOf(Build.MODEL) { Build.FINGERPRINT.split("/")[0] }


/**
 * 获取指纹机型
 * @return String
 */
val getFingerPrintModel get(): String = safeOf(Build.BRAND) { Build.FINGERPRINT.split("/")[1] }

