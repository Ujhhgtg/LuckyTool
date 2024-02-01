package com.luckyzyx.luckytool.hook.scopes.externalstorage

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method

object RemoveStorageLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source ExternalStorageProvider
        "com.android.externalstorage.ExternalStorageProvider".toClass().apply {
            val isNew = hasMethod { name = "shouldBlockDirectoryFromTree" }
            method {
                name = if (isNew) "shouldBlockDirectoryFromTree" else "shouldBlockFromTree"
            }.hook {
                replaceToFalse()
            }
        }
    }
}