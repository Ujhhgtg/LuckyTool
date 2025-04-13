package com.luckyzyx.luckytool.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.betterandroid.ui.extension.view.layoutInflater
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogPermissionLayoutBinding
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class PermissionUtils(val context: Context) {

    val binding = DialogPermissionLayoutBinding.inflate(context.layoutInflater)
    private val dialogBuilder = MaterialAlertDialogBuilder(context, dialogCentered).apply {
        setCancelable(false)
        setView(binding.root)
    }
    private var dialog: AlertDialog? = null

    private var notifyStatus = false
    private var storageStatus = false
    private var appListStatus = false
    private var installAppStatus = false

    fun init(): Boolean {
        notifyStatus = refreshNotifyChip()
        storageStatus = refreshStorageChip()
        appListStatus = refreshAppListChip()
        installAppStatus = refreshInstallAppChip()

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
        dialogBuilder.setOnDismissListener { context.exitModule() }
        dialog?.setOnDismissListener { context.exitModule() }
        dialog = dialogBuilder.show()
    }

    private fun refreshNotifyChip(): Boolean {
        val isGranted = XXPermissions.isGranted(context, Permission.NOTIFICATION_SERVICE)
        binding.notifyChip.apply {
            isEnabled = !isGranted
            setOnClickListener {
                if (isGranted) return@setOnClickListener
                XXPermissions.with(context).apply {
                    permission(Permission.NOTIFICATION_SERVICE)
                    request { _, _ -> init() }
                }
            }
        }
        return isGranted
    }

    private fun refreshStorageChip(): Boolean {
        val isGranted = XXPermissions.isGranted(context, Permission.MANAGE_EXTERNAL_STORAGE)
        binding.storageChip.apply {
            isEnabled = !isGranted
            setOnClickListener {
                if (isGranted) return@setOnClickListener
                XXPermissions.with(context).apply {
                    permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    request { _, _ -> init() }
                }
            }
        }
        return isGranted
    }

    private fun refreshAppListChip(): Boolean {
        val isGranted = XXPermissions.isGranted(context, Permission.GET_INSTALLED_APPS)
        binding.appListChip.apply {
            isEnabled = !isGranted
            setOnClickListener {
                if (isGranted) return@setOnClickListener
                XXPermissions.with(context).apply {
                    permission(Permission.GET_INSTALLED_APPS)
                    request { _, _ -> init() }
                }
            }
        }
        return isGranted
    }

    private fun refreshInstallAppChip(): Boolean {
        val isGranted = XXPermissions.isGranted(context, Permission.REQUEST_INSTALL_PACKAGES)
        binding.installAppChip.apply {
            isEnabled = !isGranted
            setOnClickListener {
                if (isGranted) return@setOnClickListener
                XXPermissions.with(context).apply {
                    permission(Permission.REQUEST_INSTALL_PACKAGES)
                    request { _, _ -> init() }
                }
            }
        }
        return isGranted
    }

    private fun toastDenied(permission: String) {
        context.showToast(context.getString(R.string.permission_denied_toast, permission))
    }

    private fun toastError(permission: String) {
        context.showToast(context.getString(R.string.permission_error_toast, permission))
    }
}