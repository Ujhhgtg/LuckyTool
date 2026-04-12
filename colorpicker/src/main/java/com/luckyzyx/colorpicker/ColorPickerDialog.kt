package com.luckyzyx.colorpicker

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyzyx.colorpicker.databinding.DialogColorPickerBinding

class ColorPickerDialog private constructor(
    context: Context,
    initialColor: Int,
    private val onColorSelected: (Int, String) -> Unit
) : MaterialAlertDialogBuilder(
    context,
    com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
) {

    val binding = DialogColorPickerBinding.inflate(LayoutInflater.from(context))

    private var currentColor: Int

    init {
        val tvColorInt = binding.tvColorInt
        val tvColorHex = binding.tvColorHex

        val colorGradientView = binding.colorGradientView
        val seekbarAlpha = binding.sliderAlpha
        val tvAlphaValue = binding.tvAlphaValue

        val colorPreview = binding.colorPreview

        // 设置初始颜色
        currentColor = initialColor
        colorPreview.setBackgroundColor(currentColor)
        updateColorText(currentColor, tvColorHex, tvColorInt)
        colorGradientView.setColor(currentColor)

        val alpha = Color.alpha(currentColor)
        seekbarAlpha.progress = alpha
        tvAlphaValue.text = alpha.toString()

        // 渐变视图颜色变化监听
        colorGradientView.setOnColorChangedListener(object :
            ColorGradientView.OnColorChangedListener {
            override fun onColorChanged(colorInt: Int, colorHex: String) {
                currentColor = colorInt
                colorPreview.setBackgroundColor(currentColor)
                updateColorText(currentColor, tvColorHex, tvColorInt)
            }
        })

        // Alpha 滑条监听
        seekbarAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, value: Int, isUser: Boolean
            ) {
                tvAlphaValue.text = value.toString()
                colorGradientView.setAlpha(value)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
        setTitle(context.getString(R.string.select_color))
        setView(binding.root)
        setPositiveButton(android.R.string.ok) { _, _ ->
            val finalColor = colorGradientView.getCurrentColor()
            val hex = colorIntToHex(finalColor)
            onColorSelected(finalColor, hex)
        }
        setNeutralButton(android.R.string.cancel, null)
        create()
    }

    private fun updateColorText(color: Int, hexText: TextView, intText: TextView) {
        hexText.text = colorIntToHex(color)
        intText.text = color.toString()
    }

    private fun colorIntToHex(color: Int): String {
        return String.format("#%08X", color)
    }

    class Builder(private val context: Context) {
        private var initialColor = Color.WHITE
        private var listener: ((Int, String) -> Unit)? = null

        fun setInitialColor(color: Int) = apply {
            this.initialColor = color
        }

        fun setInitialColor(colorHex: String) = apply {
            this.initialColor = try {
                colorHex.toColorInt()
            } catch (_: Exception) {
                Color.WHITE
            }
        }

        fun setOnColorSelectedListener(listener: (Int, String) -> Unit) = apply {
            this.listener = listener
        }

        fun build(): ColorPickerDialog {
            return ColorPickerDialog(context, initialColor, listener ?: { _, _ -> })
        }

        fun show() {
            build().show()
        }
    }
}