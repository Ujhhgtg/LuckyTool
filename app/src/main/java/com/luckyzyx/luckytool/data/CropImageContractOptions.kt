package com.luckyzyx.luckytool.data

import android.net.Uri
import com.canhub.cropper.CropImageOptions
import com.joom.paranoid.Obfuscate

@Obfuscate
data class CropImageContractOptions(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
)
