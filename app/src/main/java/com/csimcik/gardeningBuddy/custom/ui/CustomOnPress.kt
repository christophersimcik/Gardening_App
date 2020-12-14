package com.csimcik.gardeningBuddy.custom.ui

import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.MotionEvent
import android.view.View
import com.csimcik.gardeningBuddy.extensions.calculateDistance

class CustomOnPress : View.OnTouchListener {

    companion object {
        const val CLICK_DETECTION_THRESHOLD = 20
        const val DELAY = 150L
        const val TAG = "DESAT_IMAGE_VIEW"
    }

    private val initialPoint = PointF()
    override fun onTouch(view: View, event: MotionEvent?): Boolean {
        event?.let { motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialPoint.set(motionEvent.x, motionEvent.y)
                    view.isPressed = true
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val point = PointF(motionEvent.x, motionEvent.y)
                    if (isInBounds(Size(view.width, view.height), point) && isProximal(point)) {
                        delayIsPressedToOff(view)
                        view.performClick()
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    delayIsPressedToOff(view)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val point = PointF(motionEvent.x, motionEvent.y)
                    if (!isInBounds(Size(view.width, view.height), point) || !isProximal(point)) {
                        delayIsPressedToOff(view)
                    }
                    true
                }
                else -> {
                    return true
                }
            }

        }
        return true
    }

    private fun isInBounds(size: Size, point: PointF): Boolean {
        return point.x > 0 && point.x < size.width && point.y > 0 && point.y < size.height
    }

    private fun isProximal(point: PointF): Boolean {
        return point.calculateDistance(initialPoint) <= CLICK_DETECTION_THRESHOLD
    }

    private fun delayIsPressedToOff(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({ view.isPressed = false }, DELAY)
    }


}