package com.luckyzyx.luckytool.hook.scopes.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBrowserWindowLimitNumber(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source TabManager
        dexKitBridge.findClass {
            matcher {
                className("com.android.browser.TabManager")
            }
        }.findMethod {
            matcher {
                paramCount(0)
                returnType(IntType)
                usingStrings("TabManager", "multiWindowPerf")
            }
        }.apply {
            checkDataList("RemoveBrowserWindowLimitNumber")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    emptyParam()
                    returnType = IntType
                }.hook {
                    replaceTo(999)
                }
            }
        }
    }
}