package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlphabetView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "ALPHABET_VIEW"
            const val DELAY = 350L
    }
    private var increment = 0
    private val alphabet = context.resources.getString(R.string.alphabet).split(",")
    private val indices: MutableList<Index> = ArrayList()
    private val inactiveColor = ContextCompat.getColor(context, R.color.light_gray)
    private val activeColor = ContextCompat.getColor(context, R.color.index_hi_lite_circle_green)
    private val inactiveSize = context.resources.getDimension(R.dimen.index_text_sz)
    private val activeSize = context.resources.getDimension(R.dimen.sm_text)
    lateinit var onIndexChangedListener: OnIndexChangedListener
    private val paint = Paint().apply {
        color = inactiveColor
        textAlign = Paint.Align.CENTER
        textSize = inactiveSize
        typeface = Typeface.DEFAULT_BOLD
    }

    var isNotBeingScrolled = true

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        increment = h / alphabet.size + 1
        makeIndices(w / 2)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        indices.forEach {
            if (it.isHilighted) {
                paint.color = activeColor
                paint.textSize = activeSize
            } else {
                paint.color = inactiveColor
                paint.textSize = inactiveSize
            }
            canvas?.drawText(it.index, it.pos.x, it.pos.y, paint)
        }
    }

    private fun makeIndices(halfOfWidth: Int) {
        var yVal = increment / 2
        for (letter in alphabet) {
            indices.add(
                Index(
                    increment,
                    letter,
                    PointF(
                        halfOfWidth.toFloat(),
                        yVal.toFloat()
                    ),
                    this
                )
            )
            yVal += increment
        }
    }

    class Index(
        val increment: Int,
        val index: String,
        val pos: PointF,
        val parent: AlphabetView,
        var active: Boolean = false
    ) {
        var isHilighted = active

        private fun deactivate() {
            CoroutineScope(Dispatchers.Main).launch {
                delay(DELAY)
                isHilighted = false
                parent.invalidate()
            }
        }

        fun activateByRecyclerViewScroll(string : String){
            if(index == string){
                active = true
                isHilighted = true
            }else{
                active = false
                isHilighted = false
            }
        }

        fun checkActive(y: Float) {
            val hitbox = (increment/2)
            active = y >= pos.y - hitbox  && y <= pos.y + hitbox
            if (isHilighted && !active) {
                deactivate()
            }
            if (active){
                isHilighted = true
                parent.onIndexChangedListener.onIndexChanged(index)
            }
            parent.invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "on down")
                isNotBeingScrolled = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.y
                indices.forEach { it.checkActive(y) }
                Log.d(TAG, "on move")
                return true
            }
            MotionEvent.ACTION_UP -> {
                val y = event.y
                for (index in indices) {
                    index.checkActive(y)
                    if (index.active) {
                        Log.d(TAG, "value is ${index.index}")
                        performClick()
                        break
                    }
                }
                isNotBeingScrolled = true
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        Log.d(TAG, "alphabetical index view clicked")
        return super.performClick()
    }

    fun updateFromRecyclerViewScroll(string : String){
        indices.forEach {
            it.activateByRecyclerViewScroll(string)
        }
        invalidate()
    }

    interface OnIndexChangedListener {
        fun onIndexChanged(index: String)
    }
}