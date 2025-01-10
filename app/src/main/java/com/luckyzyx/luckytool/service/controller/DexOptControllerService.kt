package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IDexOptController
import com.luckyzyx.luckytool.service.PackageService
import com.topjohnwu.superuser.ipc.RootService

@Obfuscate
class DexOptControllerService : RootService() {

    override fun onBind(intent: Intent) = object : IDexOptController.Stub() {
        override fun clearApplicationProfileData(packageName: String) {
            PackageService.clearApplicationProfileData(packageName)
        }

        override fun performDexOptMode(packageName: String): Boolean {
            return PackageService.performDexOptMode(packageName)
        }
    }

}