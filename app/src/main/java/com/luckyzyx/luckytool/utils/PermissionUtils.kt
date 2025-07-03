package com.luckyzyx.luckytool.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.betterandroid.ui.extension.view.layoutInflater
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogPermissionLayoutBinding
import com.luckyzyx.luckytool.ui.activity.MainActivity
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class PermissionUtils(val context: Context) {

    val binding = DialogPermissionLayoutBinding.inflate(context.layoutInflater)
    private val dialog = MaterialAlertDialogBuilder(context, dialogCentered).apply {
        setCancelable(false)
        setView(binding.root)
    }.create()

    private var notifyStatus = false
    private var storageStatus = false
    private var appListStatus = false
    private var installAppStatus = false

    private var allowDismiss = false

    fun init() {
        notifyStatus = refreshNotifyChip()
        storageStatus = refreshStorageChip()
        appListStatus = refreshAppListChip()
        installAppStatus = refreshInstallAppChip()

        allowDismiss = notifyStatus && storageStatus && appListStatus && installAppStatus
        if (allowDismiss) dialog.setOnDismissListener(null)
        dialog.dismiss()
    }

    fun start() {
        init()
        if (allowDismiss) return
        dialog.setOnDismissListener {
            (context as MainActivity).restart()
        }
        dialog.show()
    }

    private fun refreshNotifyChip(): Boolean {
        val isGranted = XXPermissions.isGrantedPermissions(context, Permission.NOTIFICATION_SERVICE)
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
        val isGranted =
            XXPermissions.isGrantedPermissions(context, Permission.MANAGE_EXTERNAL_STORAGE)
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
        val isGranted = XXPermissions.isGrantedPermissions(context, Permission.GET_INSTALLED_APPS)
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
        val isGranted =
            XXPermissions.isGrantedPermissions(context, Permission.REQUEST_INSTALL_PACKAGES)
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