package com.luckyzyx.luckytool.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.luckyzyx.luckytool.hook.YukiEntry.onHookEntry
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object MainHook : IYukiHookXposedInit {

    override fun onInit() = YukiEntry.configs

    override fun onHook() = encase {
        onHookEntry()
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            XposedEntry.onLoadPackage(lpparam)
        }
        YukiXposedEvent.onInitZygote { startupParam: IXposedHookZygoteInit.StartupParam ->
            XposedEntry.onInitZygote(startupParam)
        }
    }
}