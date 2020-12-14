package com.csimcik.gardeningBuddy.custom.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.csimcik.gardeningBuddy.R

class AutoSizeEditTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), TextWatcher,
    ValueAnimator.AnimatorUpdateListener {

    companion object {
        const val TAG = "AUTO_SIZE_EDIT_VIEW"
        const val DEFAULT_MIN = 1f
        const val DEFAULT_MAX = 30f
        const val DURATION = 500L
        const val SCALE_FACTOR = 0.7f
    }

    private var min = DEFAULT_MIN
    private var max = DEFAULT_MAX
    private var smallerTextSz: Float
    private var currentTextSz: Float
    private var largerTextSz: Float
    private var xVal = 0f
    private var yVal = 0f
    private val hintPaint = Paint()
    private lateinit var listener: TextChangedCallback

    init {
        applyAttributes(getAttributes(attrs))
        hintPaint.apply {
            color = Color.YELLOW
            paint.textSize = DEFAULT_MAX
            smallerTextSz = paint.textSize - 1
            currentTextSz = paint.textSize
            largerTextSz = paint.textSize + 1
        }
        maxLines = super.getMaxLines()
        isPressed = super.isPressed()
        typeface = super.getTypeface()
        this.hint = super.getHint()
        this.setTextIsSelectable(true)
        this.setTextColor(super.getTextColors())
        this.setWillNotDraw(false)
    }

    private fun getAttributes(attrs: AttributeSet?): TypedArray {
        return context.theme.obtainStyledAttributes(attrs, R.styleable.AutoSizeEditTextView, 0, 0)
    }

    private fun applyAttributes(attrs: TypedArray) {
        min = attrs.getDimension(R.styleable.AutoSizeEditTextView_minSz, DEFAULT_MIN)
        max = attrs.getDimension(R.styleable.AutoSizeEditTextView_maxSz, DEFAULT_MAX)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "view width = $width")
        Log.d(TAG, "view height = $height")
        when {
            width > 0 -> {
                if (text.isNullOrBlank()) {
                    checkBounds(hint.toString())
                } else {
                    checkBounds(text.toString())
                }
            }
        }

        xVal = w / 2f
        yVal = h / 2f
    }

    override fun onTextChanged(
        text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        createValueAnimator().start()
        if (text?.isNotBlank() == true) checkBounds(text.toString())
    }

    override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        val txt = editable ?: ""
        if (txt.isBlank()) {
            checkBounds(hint.toString())
        } else
            if (this::listener.isInitialized) listener.onTextChanged(editable.toString())
        invalidate()
    }

    fun registerListener(listener: TextChangedCallback) {
        this.listener = listener
    }

    private fun checkBounds(text: String) {
        canIncrease(text)
        canDecrease(text)
        requestLayout()
    }

    private fun canIncrease(text: String) {
        val textBounds = Rect()
        paint.textSize = largerTextSz
        paint.getTextBounds(text, 0, text.length, textBounds)
        if (largerTextSz <= max && (textBounds.width() < this.width * SCALE_FACTOR || textBounds.height() < this.height * SCALE_FACTOR)) {
            increase()
            canIncrease(text)
        }
        paint.textSize = currentTextSz
    }

    private fun canDecrease(text: String) {
        val textBounds = Rect()
        paint.textSize = smallerTextSz
        paint.getTextBounds(text, 0, text.length, textBounds)
        if (smallerTextSz >= min && (textBounds.width() > this.width * SCALE_FACTOR || textBounds.height() > this.height * SCALE_FACTOR)) {
            decrease()
            canDecrease(text)
        }
        paint.textSize = currentTextSz
    }

    private fun increase() {
        smallerTextSz++
        currentTextSz++
        largerTextSz++
        Log.d(TAG, "lg = $largerTextSz sm = $smallerTextSz")
    }

    private fun decrease() {
        smallerTextSz--
        currentTextSz--
        largerTextSz--
    }

    private fun createValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(.5f, 1f).apply {
            duration = DURATION
            addUpdateListener(this@AutoSizeEditTextView)
        }
    }

    override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
        val scale = valueAnimator?.animatedValue as Float
        this.alpha = scale
    }

    interface TextChangedCallback {
        fun onTextChanged(string: String)
    }
}