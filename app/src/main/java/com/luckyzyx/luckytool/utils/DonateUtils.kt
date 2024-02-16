package com.luckyzyx.luckytool.utils

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogDonateLayoutBinding

@Suppress("unused")
object DonateData {
    private const val CNU = "Null"
    private const val CQQ = "QQ"
    private const val CQQHB = "QQHB"
    private const val CWC = "WeChat"
    private const val CAP = "AliPay"
    private const val CPP = "PayPal"

    fun showQRCode(context: Context, base64: String) {
        val binding = DialogDonateLayoutBinding.inflate(LayoutInflater.from(context))
        MaterialAlertDialogBuilder(context, dialogCentered).apply {
            setTitle(context.getString(R.string.qq))
            setView(binding.root)
        }.show()
        binding.donateImage.setImageBitmap(base64ToBitmap(base64))
        binding.donateMessage.text = context.getString(R.string.donate_message)
    }
}