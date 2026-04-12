package com.luckyzyx.colorpicker

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class ColorPickerPreference : Preference {

    private var colorValue = Color.TRANSPARENT
    private var colorHexValue = "#00000000"

    private var colorPreview: View? = null

    init {
        widgetLayoutResource = R.layout.preference_widget_color_preview
    }

    constructor(context: Context) : super(context)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        colorPreview = holder.findViewById(R.id.colorPreview)
        updatePreview()

        // 直接点击整个 item 触发
        holder.itemView.setOnClickListener {
            showColorPickerDialog()
        }

        // 也可以单独点击预览图触发
        colorPreview?.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun showColorPickerDialog() {
        ColorPickerDialog.Builder(context).apply {
            setInitialColor(colorValue)
            setOnColorSelectedListener { colorInt, colorHex ->
                setValue(colorInt, colorHex)
            }
        }.show()
    }

    private fun updatePreview() {
        colorPreview?.setBackgroundColor(colorValue)
    }

    private fun updateSummary() {
        summary = context.getString(R.string.current_color, colorHexValue)
    }

    private fun setValue(colorInt: Int, colorHex: String) {
        val oldColor = colorValue
        val oldHex = colorHexValue

        colorValue = colorInt
        colorHexValue = colorHex

        if (shouldPersist()) {
            persistString(colorHex)
        }

        if (!callChangeListener(colorHex)) {
            colorValue = oldColor
            colorHexValue = oldHex
            if (shouldPersist()) {
                persistString(oldHex)
            }
        }

        updatePreview()
        updateSummary()
    }

    private fun colorIntToHex(color: Int): String {
        return String.format("#%08X", color)
    }

    override fun onGetDefaultValue(typedArray: TypedArray, index: Int): Any {
        return "#00000000"
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        val hex = when (defaultValue) {
            is String -> defaultValue
            is Int -> colorIntToHex(defaultValue)
            else -> "#00000000"
        }

        colorHexValue = if (shouldPersist()) {
            getPersistedString(hex)
        } else {
            hex
        }

        try {
            colorValue = colorHexValue.toColorInt()
        } catch (_: Exception) {
            colorValue = Color.TRANSPARENT
            colorHexValue = "#00000000"
        }

        updatePreview()
        updateSummary()
    }
}