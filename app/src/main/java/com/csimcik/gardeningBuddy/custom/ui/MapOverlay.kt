package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.csimcik.gardeningBuddy.R

class MapOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "MAP_OVERLAY"
        const val DELAY =  10.0f
        const val BASE_SCALE = 1.0f
        const val BASE_SIZE = 150.0f
    }

    private var ripple = Ripple()
    private var interpolate = DELAY
    private var rippleColor = Color.DKGRAY
        set(value) {
            field = value
            paint.color = rippleColor
        }

    private val rippler = object : Runnable {
        override fun run() {
            if (ripple.canRipple()) {
                ripple.update()
                handler.postDelayed(this, interpolate.toLong())
                    interpolate *= .95f
            } else {
                ripple.show = false
            }
            invalidate()
        }
    }

    private fun getAttributes(attrs: AttributeSet): TypedArray {
        return context.theme.obtainStyledAttributes(attrs, R.styleable.MapOverlay, 0, 0)

    }

    private fun applyAttributes(typedArray: TypedArray) {
        rippleColor = typedArray.getColor(R.styleable.MapOverlay_rippleColor, Color.DKGRAY)
    }

    private var paint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    init {
        attrs?.let { applyAttributes(getAttributes(attrs)) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { if (ripple.show) ripple.draw(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(rippler)
    }

    fun click (point: Point) {
        ripple.ripple(point.x.toFloat(), point.y.toFloat())
    }

    inner class Ripple(
        private val location: PointF = PointF(0.0f, 0.0f),
        private var width: Float = BASE_SIZE,
        private var scaleFactor: Float = BASE_SCALE,
        var show: Boolean = false
    ) {

        fun canRipple(): Boolean {
            return scaleFactor < 6.0f
        }

        fun ripple(x: Float, y: Float) {
            location.set(x, y)
                reset()
                show = true
                handler.post(rippler)
        }

        fun update() {
            scaleFactor *= 1.125f
            if(width > BASE_SIZE/2F) width *= .90f
        }

        fun draw(canvas: Canvas) {
            paint.strokeWidth = width
            canvas.drawCircle(location.x, location.y, BASE_SIZE * scaleFactor, paint)
        }

        private fun reset() {
            interpolate = DELAY
            handler.removeCallbacks(rippler)
            scaleFactor = BASE_SCALE
            width = BASE_SIZE
        }
    }
}