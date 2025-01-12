package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IPackageServiceController
import com.luckyzyx.luckytool.service.PackagesService
import com.topjohnwu.superuser.ipc.RootService

@Obfuscate
class PackageControllerService : RootService() {

    override fun onBind(intent: Intent) = object : IPackageServiceController.Stub() {
        override fun clearApplicationProfileData(packageName: String) {
            PackagesService.clearApplicationProfileData(packageName)
        }

        override fun performDexOptMode(packageName: String): Boolean {
            return PackagesService.performDexOptMode(packageName)
        }
    }

}