package com.csimcik.gardeningBuddy.custom.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.extensions.doubleIt
import com.csimcik.gardeningBuddy.extensions.half

class MeterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    companion object {
        const val TAG = "METER_VIEW"
        const val SLOW = 1000
        const val MEDIUM = 500
        const val DEFAULT_MAX = 10
        const val DEFAULT_STROKE_WIDTH = 3f
    }

    var innerColor = Color.GRAY
        set(value) {
            field = value
            if (this::meter.isInitialized) meter.updateColor(value)
        }

    private var outerColor = Color.DKGRAY
        set(value) {
            field = value
            if (this::border.isInitialized) border.updateColor(value)
        }

    private var strokeWidth = DEFAULT_STROKE_WIDTH
        set(value) {
            field = value
            if (this::border.isInitialized) border.updateStrokeWidth(strokeWidth)
        }

    private var radius = 0f
        set(value) {
            field = value
            if (this::meter.isInitialized) meter.updateRadius(value)
        }

    var position = 0
        set(value) {
            field = value
            valueAnimator = createValueAnimator()
            startAnimation()
        }

    private var speed = MEDIUM
        set(value) {
            field = value
            valueAnimator = createValueAnimator()
        }

    var isOnScreen = false
        set(value) {
            if (field != value && value) startAnimation()
            Log.d(TAG, "is on screen = $isOnScreen")
            field = value
        }

    private var progress = 0f
    private var paddingEnd = 0f
    private var paddingTop = 0f
    private var paddingStart = 0f
    private var paddingBottom = 0f
    private lateinit var meter: Meter
    private var max = DEFAULT_MAX
    private lateinit var border: Border
    private var valueAnimator: ValueAnimator
    private var backgroundAnimator: ValueAnimator

    init {
        applyCustomAttr(getAttributes(attrs))
        valueAnimator = createValueAnimator()
        backgroundAnimator = createBackgroundValueAnimator()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // initiate objects
        createClasses(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            if (this::border.isInitialized) border.drawShape(it)
            if (position < 0) {
                if (this::meter.isInitialized) meter.drawText(it)
            } else {
                if (this::meter.isInitialized) meter.drawShape(it)
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val diameter = radius.doubleIt()
        val necessaryHeight = (diameter + getVerticalPadding()).toInt()
        val necessaryWidth = (diameter + getHorizontalPadding()).toInt()
        val width = MeasureSpec.getSize(widthMeasureSpec).coerceAtLeast(necessaryWidth)
        val height = MeasureSpec.getSize(heightMeasureSpec).coerceAtLeast(necessaryHeight)
        setMeasuredDimension(width, height)
    }

    private fun getVerticalPadding(): Float {
        return paddingTop + paddingBottom
    }

    private fun getHorizontalPadding(): Float {
        return paddingStart + paddingEnd
    }

    private fun getAttributes(attrs: AttributeSet?): TypedArray {
        return context.theme.obtainStyledAttributes(attrs, R.styleable.MeterView, 0, 0)
    }

    private fun applyCustomAttr(attr: TypedArray) {
        try {
            speed = attr.getInt(R.styleable.MeterView_speed, SLOW)
            max = attr.getInt(R.styleable.MeterView_max, DEFAULT_MAX)
            radius = attr.getDimension(R.styleable.MeterView_radius, 0f)
            outerColor = attr.getColor(R.styleable.MeterView_outerColor, Color.GRAY)
            innerColor = attr.getColor(R.styleable.MeterView_innerColor, Color.DKGRAY)
            paddingEnd = attr.getDimension(R.styleable.MeterView_paddingEnd, 0f)
            paddingTop = attr.getDimension(R.styleable.MeterView_paddingTop, 0f)
            paddingStart = attr.getDimension(R.styleable.MeterView_paddingStart, 0f)
            paddingBottom = attr.getDimension(R.styleable.MeterView_paddingBottom, 0f)
            strokeWidth = attr.getDimension(R.styleable.MeterView_strokeWidth, DEFAULT_STROKE_WIDTH)
        } finally {
            attr.recycle()
        }
    }

    open inner class Meter(
        var radius: Float,
        var start: Float,
        var end: Float,
        var progress: Float,
        var midHeight: Int
    ) {
        private val shape = RectF()
        private val paint: Paint

        init {
            initiateShape()
            paint = Paint().apply {
                color = innerColor
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                textSize = 30f
                typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
            }
        }

        fun initiateShape() {
            shape.left = start
            shape.right = end * (progress / if (max != 0) max else 1)
            shape.top = midHeight - radius
            shape.bottom = midHeight + radius
        }

        fun updateProgress() {
            shape.right = end * (progress / if (max != 0) max.toFloat() else 1f)
            this@MeterView.invalidate()
        }

        fun updateColor(color: Int) {
            paint.color = color
            this@MeterView.invalidate()
        }

        fun updateRadius(radius: Float) {
            this.radius = radius
            initiateShape()
            this@MeterView.invalidate()
        }

        fun drawShape(canvas: Canvas) {
            swapColor()
            canvas.drawRoundRect(shape, radius, radius, paint)
        }

        fun drawText(canvas: Canvas) {
            swapTextColor()
            val bounds = Rect()
            val txt = "NO DATA"
            paint.getTextBounds(txt, 0, txt.length, bounds)
            canvas.drawText(txt, width / 2f, (height / 2f) + (bounds.height() / 2f), paint)
        }

        private fun swapColor() {
            paint.color = innerColor
        }

        private fun swapTextColor() {
            paint.color = ContextCompat.getColor(context, R.color.light_background)
        }
    }

    inner class Border(
        var radius: Float,
        var start: Float,
        var end: Float,
        var progress: Float,
        var midHeight: Int
    ) {
        private val shape = RectF()
        private val paint: Paint

        init {
            initiateShape()
            paint = Paint().apply {
                color = outerColor
                isAntiAlias = true
            }
        }

        private fun initiateShape() {
            shape.left = start
            shape.right = end
            shape.top = midHeight - radius
            shape.bottom = midHeight + radius
        }

        fun updateColor(color: Int) {
            paint.color = color
        }

        fun updateStrokeWidth(strokeWidth: Float) {
            paint.strokeWidth = strokeWidth
        }

        fun updateProgress() {
            shape.right = end * (progress / if (max != 0) max.toFloat() else 1f)
            this@MeterView.invalidate()
        }

        fun drawShape(canvas: Canvas) {
            canvas.drawRoundRect(shape, radius, radius, paint)
        }

    }

    fun startAnimation() {
        valueAnimator.start()
        backgroundAnimator.start()
    }

    override fun onAnimationUpdate(animator: ValueAnimator?) {
        when (animator) {
            valueAnimator -> {
                if (this::meter.isInitialized) {
                    meter.progress = animator.animatedValue as Float
                    meter.updateProgress()
                }
            }
            backgroundAnimator -> {
                if (this::meter.isInitialized) {
                    border.progress = animator.animatedValue as Float
                    border.updateProgress()
                }
            }
        }
    }

    private fun createValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, position.toFloat()).apply {
            duration = speed.toLong()
            interpolator = DecelerateInterpolator()
            addUpdateListener(this@MeterView)
        }
    }

    private fun createBackgroundValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 10f).apply {
            duration = speed.toLong() + 250L
            interpolator = DecelerateInterpolator()
            addUpdateListener(this@MeterView)
        }
    }

    private fun createClasses(width: Int, height: Int){
        border = Border(
            radius,
            paddingStart,
            width - paddingEnd,
            progress,
            height.half()
        )
        meter = Meter(
            radius,
            paddingStart,
            width - paddingEnd,
            progress,
            height.half()
        )
    }

}