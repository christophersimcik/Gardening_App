package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R
import java.util.*
import kotlin.collections.ArrayList


class ColorsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val radius = 10f
        const val TAG = "COLORS_VIEW"
    }

    init {
        findColor("dark blue")
    }

    var listOfColors = ArrayList<String>()
        set(value) {
            field = value
            populateColors()
        }

    private val colors = ArrayList<MyColor>()
    private var increment = 50f
    private var yPos = 0f
    private var xPos = increment
    lateinit var listener: ColorTouchCallback
    private val bounds = RectF()
    private val borderColor = ContextCompat.getColor(context, R.color.Black)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xPos = radius * 2
        yPos = h / 2f + (radius / 4)
        populateColors()
        setViewBounds(height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        colors.forEach { it.draw(canvas) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width.coerceAtLeast(xPos.toInt()), height)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                colors.forEach {
                    if (it.isTouched(event.x, event.y)) {
                        Log.d(TAG, "is touched")
                        if (this::listener.isInitialized) {
                            listener.onDown(this, it.color.name)
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (this::listener.isInitialized) {
                    listener.onUp(this)
                }
                this.performClick()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "x = ${event.x} y = ${event.y}")
                if (this::listener.isInitialized) {
                    listener.onCancel(this)
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                colors.forEach {
                    if (it.isTouched(event.x, event.y)) {
                        Log.d(TAG, "is touched")
                        if (this::listener.isInitialized) {
                            listener.onMove(this, it.color.name)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    private fun populateColors() {
        colors.clear()
        for (color in listOfColors) {
            val position = PointF(xPos, yPos)
            val myColor = findColor(color)
            colors.add(MyColor(position, myColor, radius, borderColor))
            xPos += increment + radius
        }
        forceLayout()
        invalidate()
    }

    class MyColor(
        private val position: PointF,
        val color: ColorInfo,
        private val radius: Float = 0f,
        private val borderColor: Int = Color.BLACK
    ) {

        private val paint = Paint().apply { color = this.color; isAntiAlias = true }
        val threshold = (radius + 25f) - 2
        private val bounds = RectF(
            position.x - threshold,
            position.y - threshold,
            position.x + threshold,
            position.y + threshold
        )

        fun draw(canvas: Canvas?) {
            drawInner(canvas)
            drawOuter(canvas)
        }

        fun isTouched(x: Float, y: Float): Boolean {
            return x > bounds.left && x < bounds.right && y > bounds.top && y < bounds.bottom
        }

        private fun drawInner(canvas: Canvas?) {
            paint.color = color.value
            paint.style = Paint.Style.FILL
            canvas?.let {
                canvas.drawCircle(position.x, position.y, radius, paint)
            }
        }

        private fun drawOuter(canvas: Canvas?) {
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas?.let {
                canvas.drawCircle(position.x, position.y, radius, paint)
            }
        }
    }

    private fun findColor(color: String): ColorInfo {
        val match = color.split(" ").joinToString(" ") { it.capitalize(Locale.ROOT) }
        val colors = resources.obtainTypedArray(R.array.colors)
        val names = resources.getStringArray(R.array.colorNames)
        for (i in names.indices) {
            if (names[i] == match) {
                val name = names[i]
                val value = colors.getColor(i, Color.TRANSPARENT)
                return ColorInfo(name, value)
            }
        }
        colors.recycle()
        return ColorInfo(color, Color.TRANSPARENT)
    }

    private fun setViewBounds(height: Int) {
        bounds.set(0f, 0f, xPos, height.toFloat())
        Log.d(
            TAG,
            "left ${bounds.left} right ${bounds.right} top ${bounds.top} bot ${bounds.bottom}"
        )

    }

    class ColorInfo(
        val name: String,
        val value: Int
    )

    interface ColorTouchCallback {
        fun onMove(view: View, color: String)
        fun onDown(view: View, color: String)
        fun onUp(view: View)
        fun onCancel(view: View)
    }

}

