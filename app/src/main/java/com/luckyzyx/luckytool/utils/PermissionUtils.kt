package com.luckyzyx.luckytool.utils

import android.app.Activity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luckyzyx.luckytool.R

class PermissionUtils(val context: Activity) {

    fun checkPermissions() {
        //文件读写权限
        FileUtils.checkRWPermission(context)

        //发送通知权限
//        XXPermissions.with(context).apply {
//            val permission = context.getString(R.string.get_send_notifications_permission)
//            permission(Permission.NOTIFICATION_SERVICE)
//            request(object : OnPermissionCallback {
//                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
//                }
//
//                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
//                    if (doNotAskAgain) toastDenied(permission)
//                    else toastError(permission)
//                }
//            })
//        }

        //读取应用列表权限
        XXPermissions.with(context).apply {
            val permission = context.getString(R.string.get_installed_apps_permission)
            permission(Permission.GET_INSTALLED_APPS)
            request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {

                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        toastDenied(permission)
                        XXPermissions.startPermissionActivity(context, permissions)
                        return
                    } else toastError(permission)
                }
            })
        }
    }

    private fun toastDenied(permission: String) {
        context.toast(context.getString(R.string.permission_denied_toast, permission))
    }

    private fun toastError(permission: String) {
        context.toast(context.getString(R.string.permission_error_toast, permission))
    }
}