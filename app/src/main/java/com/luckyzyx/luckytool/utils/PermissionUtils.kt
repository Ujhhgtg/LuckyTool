package com.luckyzyx.luckytool.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogPermissionLayoutBinding

@Obfuscate
class PermissionUtils(val activity: Activity) {

    val binding = DialogPermissionLayoutBinding.inflate(activity.layoutInflater)
    private val dialogBuilder = MaterialAlertDialogBuilder(activity, dialogCentered).apply {
        setCancelable(false)
        setView(binding.root)
    }
    private var dialog: AlertDialog? = null

    private var notifyStatus = false
    private var storageStatus = false
    private var appListStatus = false
    private var installAppStatus = false

    fun init(): Boolean {
        notifyStatus = XXPermissions.isGranted(activity, Permission.NOTIFICATION_SERVICE)
        refreshNotifyChip(notifyStatus)
        storageStatus = XXPermissions.isGranted(activity, Permission.MANAGE_EXTERNAL_STORAGE)
        refreshStorageChip(storageStatus)
        appListStatus = XXPermissions.isGranted(activity, Permission.GET_INSTALLED_APPS)
        refreshAppListChip(appListStatus)
        installAppStatus = XXPermissions.isGranted(activity, Permission.REQUEST_INSTALL_PACKAGES)
        refreshInstallAppChip(installAppStatus)

        val status = notifyStatus && storageStatus && appListStatus && installAppStatus
        if (status) {
            dialogBuilder.setOnDismissListener(null)
            dialog?.setOnDismissListener(null)
            dialog?.dismiss()
        }
        return status
    }

    fun start() {
        if (init()) return
        dialogBuilder.setOnDismissListener { activity.exitModule() }
        dialog?.setOnDismissListener { activity.exitModule() }
        dialog = dialogBuilder.show()
    }

    private fun refreshNotifyChip(status: Boolean) {
        binding.notifyChip.apply {
            isEnabled = !status
            if (status) return@apply
            setOnClickListener {
                XXPermissions.with(context).apply {
                    permission(Permission.NOTIFICATION_SERVICE)
                    request { _, _ -> init() }
                }
            }
        }
    }

    private fun refreshStorageChip(status: Boolean) {
        binding.storageChip.apply {
            isEnabled = !status
            if (status) return@apply
            setOnClickListener {
                XXPermissions.with(context).apply {
                    permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    request { _, _ -> init() }
                }
            }
        }
    }

    private fun refreshAppListChip(status: Boolean) {
        binding.appListChip.apply {
            isEnabled = !status
            if (status) return@apply
            setOnClickListener {
                XXPermissions.with(context).apply {
                    permission(Permission.GET_INSTALLED_APPS)
                    request { _, _ -> init() }
                }
            }
        }
    }

    private fun refreshInstallAppChip(status: Boolean) {
        binding.installAppChip.apply {
            isEnabled = !status
            if (status) return@apply
            setOnClickListener {
                XXPermissions.with(context).apply {
                    permission(Permission.REQUEST_INSTALL_PACKAGES)
                    request { _, _ -> init() }
                }
            }
        }
    }

    private fun toastDenied(permission: String) {
        activity.showToast(activity.getString(R.string.permission_denied_toast, permission))
    }

    private fun toastError(permission: String) {
        activity.showToast(activity.getString(R.string.permission_error_toast, permission))
    }
}