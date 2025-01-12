package com.luckyzyx.luckytool.service

import android.content.ComponentName
import android.content.Context
import android.content.pm.IPackageManager
import android.os.IBinder
import android.os.RemoteException
import android.os.ServiceManager
import android.os.SystemProperties
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IPackageServiceController
import com.luckyzyx.luckytool.service.controller.PackageControllerService
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
object PackagesService {
    private val TAG = "PackageService"
    private var pm: IPackageManager? = null
    private var binder: IBinder? = null
    private var controller: IPackageServiceController? = null

    fun init(context: Context) {
        get(context) {}
    }

    fun get(context: Context, result: (IPackageServiceController?) -> Unit) {
        if (controller != null) result(controller)
        else context.bindRootService(PackageControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IPackageServiceController.Stub.asInterface(iBinder)
                LogUtils.d(TAG, "get", "${controller != null}", true)
                result(controller)
            })
    }

    private val recipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            LogUtils.e(TAG, "PM DeathRecipient", "pm is dead", true)
            binder?.unlinkToDeath(this, 0)
            binder = null
            pm = null
        }
    }

    private fun getPackageManager(): IPackageManager? {
        if (binder == null || pm == null) {
            binder = ServiceManager.getService("package")
            if (binder == null) return null
            try {
                binder!!.linkToDeath(recipient, 0)
            } catch (e: RemoteException) {
                LogUtils.e(TAG, "getPackageManager", e.toString(), true)
            }
            pm = IPackageManager.Stub.asInterface(binder)
        }
        return pm
    }

    fun clearApplicationProfileData(packageName: String) {
        val pm = getPackageManager() ?: return
        pm.clearApplicationProfileData(packageName)
    }

    fun performDexOptMode(packageName: String): Boolean {
        val pm = getPackageManager() ?: return false
        return pm.performDexOptMode(
            packageName, SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false),
            SystemProperties.get("pm.dexopt.install", "speed-profile"), true, true, null
        )
    }

}