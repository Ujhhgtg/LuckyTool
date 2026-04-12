package com.luckyzyx.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get

class ColorGradientView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 当前选中的颜色（不带透明度）
    private var baseColor = Color.RED

    // 当前透明度 0-255
    private var alpha = 255

    // 指示器位置
    private var indicatorX = 0f
    private var indicatorY = 0f

    // 当前色相 0-360
    private var hue = 0f

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val indicatorFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 渐变 Bitmap 用于颜色拾取
    private var gradientBitmap: Bitmap? = null
    private var hueBitmap: Bitmap? = null

    // 回调接口
    interface OnColorChangedListener {
        fun onColorChanged(colorInt: Int, colorHex: String)
    }

    private var listener: OnColorChangedListener? = null

    // 是否显示色相条
    private var showHueBar = true

    init {
        // 初始化指示器位置
        post {
            updateIndicatorFromColor(baseColor)
            notifyColorChanged()
        }
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener) {
        this.listener = listener
    }

    fun setColor(color: Int) {
        this.baseColor = color and 0x00FFFFFF
        this.alpha = Color.alpha(color)
        updateIndicatorFromColor(baseColor)
        invalidate()
        notifyColorChanged()
    }

    fun setAlpha(alpha: Int) {
        this.alpha = alpha.coerceIn(0, 255)
        invalidate()
        notifyColorChanged()
    }

    fun getCurrentColor(): Int = (alpha shl 24) or (baseColor and 0x00FFFFFF)

    private fun updateIndicatorFromColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hue = hsv[0]

        val width = width.toFloat()
        val height = height.toFloat()
        val gradientHeight = if (showHueBar) height * 0.85f else height

        // 根据饱和度和明度计算位置
        indicatorX = hsv[1] * width
        indicatorY = (1 - hsv[2]) * gradientHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val gradientHeight = if (showHueBar) height * 0.85f else height

        // 1. 绘制主渐变区域（饱和度 x 明度）
        drawMainGradient(canvas, width, gradientHeight)

        // 2. 绘制色相条
        if (showHueBar) {
            drawHueBar(canvas, width, gradientHeight, height)
        }

        // 3. 绘制指示器
        drawIndicator(canvas)
    }

    private fun drawMainGradient(canvas: Canvas, width: Float, height: Float) {
        // 创建主渐变 Bitmap（如果尺寸改变则重建）
        if (gradientBitmap == null || gradientBitmap?.width != width.toInt() || gradientBitmap?.height != height.toInt()) {
            gradientBitmap = createMainGradientBitmap(width.toInt(), height.toInt())
        }

        gradientBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    private fun createMainGradientBitmap(w: Int, h: Int): Bitmap {
        val bitmap = createBitmap(w, h)
        val pixels = IntArray(w * h)

        val hsv = FloatArray(3)
        hsv[0] = hue  // 使用当前色相

        for (y in 0 until h) {
            // 明度：顶部亮(1.0)到底部暗(0.0)
            hsv[2] = 1f - (y.toFloat() / h)

            for (x in 0 until w) {
                // 饱和度：左边低(0.0)到右边高(1.0)
                hsv[1] = x.toFloat() / w

                val color = Color.HSVToColor(hsv)
                pixels[y * w + x] = color
            }
        }

        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

    private fun drawHueBar(canvas: Canvas, width: Float, gradientTop: Float, totalHeight: Float) {
        val barHeight = totalHeight - gradientTop
        val startY = gradientTop + 50F

        // 创建色相条 Bitmap
        if (hueBitmap == null || hueBitmap?.width != width.toInt()) {
            hueBitmap = createHueBarBitmap(width.toInt(), barHeight.toInt())
        }

        hueBitmap?.let {
            canvas.drawBitmap(it, 0f, startY, null)
        }

        // 绘制色相指示器
        val hueIndicatorX = (hue / 360f) * width

        indicatorPaint.color = Color.BLACK
        indicatorPaint.strokeWidth = 5f
        canvas.drawLine(hueIndicatorX, startY, hueIndicatorX, totalHeight, indicatorPaint)
    }

    private fun createHueBarBitmap(w: Int, h: Int): Bitmap {
        val bitmap = createBitmap(w, h)

        val colors = intArrayOf(
            Color.RED, Color.YELLOW, Color.GREEN,
            Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED
        )
        val positions = floatArrayOf(0f, 1f / 6f, 2f / 6f, 3f / 6f, 4f / 6f, 5f / 6f, 1f)

        val shader =
            LinearGradient(0f, 0f, w.toFloat(), 0f, colors, positions, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.shader = shader

        val canvas = Canvas(bitmap)
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        return bitmap
    }

    private fun drawIndicator(canvas: Canvas) {
        // 绘制指示器外圈（白色）
        indicatorPaint.color = Color.WHITE
        indicatorPaint.strokeWidth = 4f
        canvas.drawCircle(indicatorX, indicatorY, 16f, indicatorPaint)

        // 绘制指示器内圈（当前颜色）
        val currentColorWithAlpha = getCurrentColor()
        indicatorFillPaint.color = currentColorWithAlpha
        canvas.drawCircle(indicatorX, indicatorY, 12f, indicatorFillPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val gradientHeight = if (showHueBar) height * 0.85f else height.toFloat()
        val hueStartY = gradientHeight + 50F

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (showHueBar && y > hueStartY) {
                    // 触摸色相条
                    hue = (x / width).coerceIn(0f, 1f) * 360f
                    updateMainGradient()
                } else if (y <= gradientHeight) {
                    // 触摸主渐变区
                    indicatorX = x.coerceIn(0f, width.toFloat())
                    indicatorY = y.coerceIn(0f, gradientHeight)

                    // 从 Bitmap 获取颜色
                    gradientBitmap?.let { bitmap ->
                        val px = indicatorX.toInt().coerceIn(0, bitmap.width - 1)
                        val py = indicatorY.toInt().coerceIn(0, bitmap.height - 1)
                        baseColor = bitmap[px, py]
                    }
                }

                invalidate()
                notifyColorChanged()
            }
        }
        return true
    }

    private fun updateMainGradient() {
        // 重建主渐变 Bitmap
        gradientBitmap = createMainGradientBitmap(width, (height * 0.85f).toInt())

        // 根据当前指示器位置重新获取颜色
        gradientBitmap?.let { bitmap ->
            val px = indicatorX.toInt().coerceIn(0, bitmap.width - 1)
            val py = indicatorY.toInt().coerceIn(0, bitmap.height - 1)
            baseColor = bitmap[px, py]
        }
    }

    private fun notifyColorChanged() {
        val finalColor = getCurrentColor()
        val hex = String.format("#%08X", finalColor)
        listener?.onColorChanged(finalColor, hex)
    }
}