package com.luckyzyx.luckytool.hook

import android.os.Build.VERSION_CODES.R
import android.os.Build.VERSION_CODES.S
import android.os.Build.VERSION_CODES.S_V2
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
import com.highcapable.yukihookapi.hook.log.YLog
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForR
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForS
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForT
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForU
import com.luckyzyx.luckytool.hook.CorePatch.CorePatchForV
import com.luckyzyx.luckytool.hook.scopes.android.DisableFlagSecure
import com.luckyzyx.luckytool.utils.SDK
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Obfuscate
object XposedEntry {

    fun onLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "android") {
            if (lpparam.processName == "android") when (SDK) {
                VANILLA_ICE_CREAM -> CorePatchForV().handleLoadPackage(lpparam)
                UPSIDE_DOWN_CAKE -> CorePatchForU().handleLoadPackage(lpparam)
                TIRAMISU -> CorePatchForT().handleLoadPackage(lpparam)
                S, S_V2 -> CorePatchForS().handleLoadPackage(lpparam)
                R -> CorePatchForR().handleLoadPackage(lpparam)
                else -> YLog.error("[CorePatch] Unsupported Version of Android -> $SDK")
            }
        }
        DisableFlagSecure().handleLoadPackage(lpparam)
    }

    fun onInitZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        when (SDK) {
//                    VANILLA_ICE_CREAM -> CorePatchForV().initZygote(startupParam)
            UPSIDE_DOWN_CAKE -> CorePatchForU().initZygote(startupParam)
            TIRAMISU -> CorePatchForT().initZygote(startupParam)
            S, S_V2 -> CorePatchForS().initZygote(startupParam)
            R -> CorePatchForR().initZygote(startupParam)
        }
    }

}