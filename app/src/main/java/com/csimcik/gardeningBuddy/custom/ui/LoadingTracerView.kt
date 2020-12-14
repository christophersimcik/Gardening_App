package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.csimcik.gardeningBuddy.R


class LoadingTracerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "TRACER_VIEW"
        const val DURATION = 10
        const val RIGHT = 1
        const val LEFT = -1
        const val BASE = 50
        const val DEFAULT_HEAD_ALPHA = 100
        const val SPEED_MODIFIER_FACTOR = 0.0125f
        const val SPEED_MODIFIER_BASE = 1.0f
    }

    private var head = 0.0f
    private var tail = 0.0f
    private var goRight: Boolean
    private var headIsDone = false
    private var tailIsDone = false
    private var speedModifier = 1.0f
    private var tailColor = Color.TRANSPARENT
    private var direction = Direction.RIGHT.value

    private var headColor = Color.WHITE
        set(value) {
            field = makeTransparent(value)
        }

    private var headAlpha = DEFAULT_HEAD_ALPHA

    private val runnable = object : Runnable {
        override fun run() {
            updateRect()
            handler.postDelayed(this, (DURATION).toLong())
            invalidate()
        }

    }

    private val paint = Paint()
    private val rect = RectF()

    init {
        attrs?.let { applyAttributes(getAttributes(attrs)) }
        goRight = direction
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == GONE) handler.removeCallbacks(runnable)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0) {
            if (direction == Direction.LEFT.value) {
                head = w.toFloat()
                tail = w.toFloat()
            }
            setLateralRect(head, tail)
            setLongitudinalRect(h.toFloat())
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // stop animations when view is detached
        handler.removeCallbacks(runnable)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawRect(canvas)
        }
    }

    private fun drawRect(canvas: Canvas) {
        applyGradient(updateShader(rect))
        canvas.drawRect(rect, paint)
    }

    private fun applyGradient(shader: LinearGradient) {
        paint.shader = shader
    }

    private fun getAttributes(attrs: AttributeSet): TypedArray {
        return context.obtainStyledAttributes(attrs, R.styleable.LoadingTracerView, 0, 0)
    }

    private fun applyAttributes(attrs: TypedArray) {
        direction = attrs.getBoolean(
            R.styleable.LoadingTracerView_starting_direction,
            Direction.RIGHT.value
        )
        headColor = attrs.getColor(R.styleable.LoadingTracerView_headColor, Color.WHITE)
        tailColor = attrs.getColor(R.styleable.LoadingTracerView_tailColor, Color.TRANSPARENT)
        headAlpha = attrs.getInteger(R.styleable.LoadingTracerView_headAlpha, DEFAULT_HEAD_ALPHA)
    }

    private fun updateShader(rect: RectF): LinearGradient {
        return if (goRight) {
            LinearGradient(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                tailColor,
                headColor,
                Shader.TileMode.REPEAT
            )
        } else {
            LinearGradient(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                headColor,
                tailColor,
                Shader.TileMode.REPEAT
            )
        }
    }

    private fun updateRect() {
        setHeadState()
        setTailState()
        moveHead()
        moveTail()
        changeDirection()
        setLateralRect(head, tail)
        updateModifier()
        invalidate()
        Log.d(TAG," direction = $direction and goRight = $goRight")
    }

    private fun moveHead() {
        when {
            goRight -> if (!headIsDone) head += updateSpeed(RIGHT)
            !goRight -> if (!headIsDone) head += updateSpeed(LEFT)
        }
    }

    private fun moveTail() {
        when {
            goRight -> if (headIsDone && !tailIsDone) tail += updateSpeed(RIGHT)
            !goRight -> if (headIsDone && !tailIsDone) tail += updateSpeed(LEFT)
        }
    }

    private fun updateSpeed(direction: Int): Float {
        return (BASE * speedModifier).coerceAtLeast(5.0f) * direction
    }

    private fun updateModifier() {
        if (speedModifier > SPEED_MODIFIER_FACTOR) speedModifier -= SPEED_MODIFIER_FACTOR
    }

    private fun setHeadState() {
        when {
            goRight -> if (head >= width) {
                headIsDone = true
            }
            !goRight -> if (head <= 0) {
                headIsDone = true
            }
        }
    }

    private fun setTailState() {
        when {
            goRight -> if (tail >= width) {
                tailIsDone = true
            }
            !goRight -> if (tail <= 0) {
                tailIsDone = true
            }
        }
    }

    private fun changeDirection() {
        if (headIsDone && tailIsDone) {
            goRight = !goRight
            headIsDone = false
            tailIsDone = false
            speedModifier = SPEED_MODIFIER_BASE
        }
    }

    private fun setLateralRect(head: Float, tail: Float) {
        if (goRight) {
            rect.right = head
            rect.left = tail
        } else {
            rect.right = tail
            rect.left = head
        }
    }

    private fun setLongitudinalRect(bottom: Float) {
        rect.bottom = bottom
    }

    private fun makeTransparent(color: Int): Int {
        return Color.argb(headAlpha, color.red, color.green, color.blue)
    }

    enum class Direction(val value: Boolean) {
        RIGHT(true),
        LEFT(false)
    }

}