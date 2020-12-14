package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout


class RoundedFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var rectF = RectF(0f, 0f, 0f, 0f)
    private val path = Path()
    private val radii = floatArrayOf(0f,0f,0f,0f,60f,60f,60f,60f)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF = RectF(0f, 0f, w.toFloat(), h.toFloat())
        resetPath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val save: Int = canvas?.save() ?: 0
        canvas?.clipPath(path)
        canvas?.restoreToCount(save)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save: Int = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    private fun resetPath() {
        path.reset()
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        path.close()
    }
}