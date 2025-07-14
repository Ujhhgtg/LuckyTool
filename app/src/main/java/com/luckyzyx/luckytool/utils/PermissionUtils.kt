package com.luckyzyx.luckytool.utils

import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.luckyzyx.luckytool.R
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class PermissionUtils(val context: Context) {

    fun start() {
        XXPermissions.with(context).apply {
            permission(PermissionLists.getManageExternalStoragePermission())
            permission(PermissionLists.getRequestInstallPackagesPermission())
            permission(PermissionLists.getGetInstalledAppsPermission())
            permission(PermissionLists.getNotificationServicePermission())
            permission(PermissionLists.getPostNotificationsPermission())
        }.request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<IPermission?>, allGranted: Boolean) {
                if (!allGranted) {
//                    toast("获取部分权限成功,部分权限未正常授予")
                    XXPermissions.startPermissionActivity(context, permissions)
                    return
                }
//                toast("获取权限成功")
            }

            override fun onDenied(permissions: List<IPermission?>, doNotAskAgain: Boolean) {
                if (doNotAskAgain) {
//                    toast("拒绝授权,请手动授予权限")
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(context, permissions)
                } else {
//                    toast("获取权限失败")
                }
            }
        })
    }

    private fun toastDenied(permission: String) {
        context.showToast(context.getString(R.string.permission_denied_toast, permission))
    }

    private fun toastError(permission: String) {
        context.showToast(context.getString(R.string.permission_error_toast, permission))
    }
}