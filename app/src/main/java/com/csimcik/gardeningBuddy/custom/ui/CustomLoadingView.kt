package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.extensions.halfAsFloat

class CustomLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "CUSTOM_LOADING_VIEW"
        const val DURATION = 41.666f
    }

    private val runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, DURATION.toLong())
            paceTail()
            updateHeadPosition()
            updateTailPosition()
            invalidate()
        }
    }

    private var widthOfRing = 45

    private var headPosition = 0.0f
    private var tailPosition = 0.0f

    private var tailCanMove = false

    private val paint = Paint()

    private var color: Int = ContextCompat.getColor(context, R.color.LightGreen)

    private val mode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    init {
        setLayerType(LAYER_TYPE_HARDWARE, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // stop animations when view is detached
        handler.removeCallbacks(runnable)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.rotate(-90f, width.halfAsFloat(), height.halfAsFloat())
            drawCircle(canvas)
            paint.xfermode = mode
            drawCenter(canvas)
            paint.xfermode = null
            it.rotate(90.0f, width.halfAsFloat(), height.halfAsFloat())
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when (visibility) {
            GONE -> handler.removeCallbacks(runnable)
        }
    }


    private fun drawCircle(canvas: Canvas) {
        paint.shader = SweepGradient(
            width.halfAsFloat(),
            height.halfAsFloat(),
            getColors(),
            getPositions()
        )
        canvas.drawCircle(
            width.halfAsFloat(),
            height.halfAsFloat(),
            width.halfAsFloat(),
            paint
        )
        paint.shader = null
    }

    private fun drawCenter(canvas: Canvas) {
        val radius = width.halfAsFloat() - widthOfRing
        canvas.drawCircle(width.halfAsFloat(), height.halfAsFloat(), radius, paint)
    }

    private fun getColors(): IntArray {
        return intArrayOf(Color.TRANSPARENT, color, Color.TRANSPARENT, Color.TRANSPARENT)
    }

    private fun getPositions(): FloatArray {
        return floatArrayOf(tailPosition, headPosition, headPosition, tailPosition)
    }

    private fun paceTail() {
        if (headPosition > .5f) tailCanMove = true
    }

    private fun updateHeadPosition() {
        if (headPosition < 1.0f) {
            headPosition += 1.0f / DURATION
        } else {
            headPosition = 1.0f
        }
    }

    private fun updateTailPosition() {
        if (tailPosition < 1.0f) {
            if (tailCanMove) {
                tailPosition += 1.0f / 41.666f
            }
        } else {
            headPosition = 0.0f
            tailPosition = 0.0f
            tailCanMove = false
        }
    }
}