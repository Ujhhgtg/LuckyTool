package com.luckyzyx.luckytool.ui.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.R
import com.canhub.cropper.parcelable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.dialogCentered

class CropImageActivity : CropImageActivity(), MenuProvider {

    private var cropImageUri: Uri? = null
    private lateinit var cropImageOptions: CropImageOptions

    private var cropImageView: CropImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMenuProvider(this, this, Lifecycle.State.RESUMED)

        val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        cropImageUri = bundle?.parcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        cropImageOptions =
            bundle?.parcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS) ?: CropImageOptions()
    }

    override fun showImageSourceDialog(openSource: (Source) -> Unit) {
        MaterialAlertDialogBuilder(this, dialogCentered).apply {
            setCancelable(false)
            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                    setResultCancel()
                    finish()
                }
                true
            }
            setTitle(R.string.pick_image_chooser_title)
            setItems(
                arrayOf(
                    getString(R.string.pick_image_camera),
                    getString(R.string.pick_image_gallery),
                ),
            ) { _, position -> openSource(if (position == 0) Source.CAMERA else Source.GALLERY) }
            show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        if (cropImageOptions.skipEditing) return
        menuInflater.inflate(R.menu.crop_image_menu, menu)

        if (!cropImageOptions.allowRotation) {
            menu.removeItem(R.id.ic_rotate_left_24)
            menu.removeItem(R.id.ic_rotate_right_24)
        } else if (cropImageOptions.allowCounterRotation) {
            menu.findItem(R.id.ic_rotate_left_24).isVisible = true
        }

        if (!cropImageOptions.allowFlipping) menu.removeItem(R.id.ic_flip_24)

        if (cropImageOptions.cropMenuCropButtonTitle != null) {
            menu.findItem(R.id.crop_image_menu_crop).title =
                cropImageOptions.cropMenuCropButtonTitle
        }

        var cropIcon: Drawable? = null
        try {
            if (cropImageOptions.cropMenuCropButtonIcon != 0) {
                cropIcon = ContextCompat.getDrawable(this, cropImageOptions.cropMenuCropButtonIcon)
                menu.findItem(R.id.crop_image_menu_crop).icon = cropIcon
            }
        } catch (e: Exception) {
            Log.w("AIC", "Failed to read menu crop drawable", e)
        }

        cropImageOptions.activityMenuIconColor =
            if (ThemeUtils.isNightMode(resources.configuration)) Color.WHITE
            else Color.BLACK

        @Suppress("KotlinConstantConditions")
        if (cropImageOptions.activityMenuIconColor != 0) {
            updateMenuItemIconColor(
                menu,
                R.id.ic_rotate_left_24,
                cropImageOptions.activityMenuIconColor,
            )
            updateMenuItemIconColor(
                menu,
                R.id.ic_rotate_right_24,
                cropImageOptions.activityMenuIconColor,
            )
            updateMenuItemIconColor(menu, R.id.ic_flip_24, cropImageOptions.activityMenuIconColor)

            if (cropIcon != null) {
                updateMenuItemIconColor(
                    menu,
                    R.id.crop_image_menu_crop,
                    cropImageOptions.activityMenuIconColor,
                )
            }
        }
        cropImageOptions.activityMenuTextColor?.let { menuItemsTextColor ->
            val menuItemIds = listOf(
                R.id.ic_rotate_left_24,
                R.id.ic_rotate_right_24,
                R.id.ic_flip_24,
                R.id.ic_flip_24_horizontally,
                R.id.ic_flip_24_vertically,
                R.id.crop_image_menu_crop,
            )
            for (itemId in menuItemIds) {
                updateMenuItemTextColor(menu, itemId, menuItemsTextColor)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
        R.id.crop_image_menu_crop -> {
            cropImage()
            true
        }

        R.id.ic_rotate_left_24 -> {
            rotateImage(-cropImageOptions.rotationDegrees)
            true
        }

        R.id.ic_rotate_right_24 -> {
            rotateImage(cropImageOptions.rotationDegrees)
            true
        }

        R.id.ic_flip_24_horizontally -> {
            cropImageView?.flipImageHorizontally()
            true
        }

        R.id.ic_flip_24_vertically -> {
            cropImageView?.flipImageVertically()
            true
        }

        android.R.id.home -> {
            setResultCancel()
            true
        }

        else -> super.onOptionsItemSelected(menuItem)
    }

    override fun setCropImageView(cropImageView: CropImageView) {
        this.cropImageView = cropImageView
    }
}