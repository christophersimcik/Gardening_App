package com.csimcik.gardeningBuddy.custom.ui

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.util.SizeF
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.csimcik.gardeningBuddy.half


class ManipulateMatrixOnTouch(val view: AppCompatImageView) : View.OnTouchListener {

    companion object {
        const val TAG = "TOUCH_HELPER"
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val SPACING_THRESHOLD = 50f
        const val DRAG_SPEED_DAMPENER = .10f
    }

    private val pointerDown = MotionEvent.ACTION_POINTER_DOWN
    private val pointerUp = MotionEvent.ACTION_POINTER_UP
    private val move = MotionEvent.ACTION_MOVE
    private val down = MotionEvent.ACTION_DOWN
    private val up = MotionEvent.ACTION_UP
    private val imageHelper = ImageHelper()
    private val start = PointF(0f, 0f)
    private val mid = PointF(0f, 0f)
    private var prevPointerDistance = 0f
    private var mode = NONE

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        view as AppCompatImageView
        event?.let {
            when (event.actionMasked) {
                down -> {
                    start.set(event.x, event.y)
                    mode = DRAG
                    moveImage(event)
                }
                pointerDown -> {
                    mode = ZOOM
                    getMidPoint(event)
                    prevPointerDistance = getPointerDistance(event)
                }
                up -> {
                    mode = NONE
                }

                pointerUp -> {
                    mode = NONE
                }
                move -> {
                    when (mode) {
                        DRAG -> moveImage(event)
                        ZOOM -> scaleImage(event)
                    }


                }
                else -> {

                }
            }
        }
        return true
    }

    private fun scaleImage(event: MotionEvent) {
        val pointerDistance = getPointerDistance(event)
        if (pointerDistance > SPACING_THRESHOLD) {
            (pointerDistance / prevPointerDistance).also {
                var factor = it
                if (factor < .0f) factor = .01f
                if (factor > 1f) factor = 1.01f
                val values = FloatArray(9)
                view.imageMatrix.getValues(values)
                val matrix = Matrix()
                matrix.set(view.imageMatrix)
                matrix.getValues(values)
                val scaleX = values[Matrix.MSCALE_X]
                val scaleY = values[Matrix.MSCALE_Y]
                val point = imageHelper.getScaling(mid)
                Log.d(TAG, "*before translation x = ${values[Matrix.MTRANS_X]}")
                if(view.drawable.intrinsicWidth * (scaleX * factor) > view.width) {
                    if(factor < 0) {
                        val x = imageHelper.centerOffsetX - values[Matrix.MTRANS_X]
                        val y = imageHelper.centerOffsetY - values[Matrix.MTRANS_Y]
                        matrix.preScale(factor, factor)
                        matrix.postTranslate(x,y)
                    }else {
                        matrix.preScale(factor, factor, point.x, point.y)
                        Log.d(TAG, "is")
                    }
                }
                /*
                }else{
                    val x = imageHelper.centerOffsetX - values[Matrix.MTRANS_X].double()
                    val y = imageHelper.centerOffsetY - values[Matrix.MTRANS_Y].double()
                    matrix.preScale(factor, factor,x,y)
                  // matrix.preTranslate(x,y)
                    Log.d(TAG, "isn't")
                }*/
                matrix.getValues(values)
                Log.d(TAG, "*after translation x = ${values[Matrix.MTRANS_X]}")
                    view.imageMatrix = matrix
                    imageHelper.update()
            }
        }

    }

    private fun getPointerDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun getMidPoint(event: MotionEvent) {
        val x: Float = event.getX(0) + event.getX(1)
        val y: Float = event.getY(0) + event.getY(1)
        mid.set(x / 2f, y / 2f)
    }

    private fun moveImage(event: MotionEvent) {
        val values = FloatArray(9)
        view.imageMatrix.getValues(values)
        var xTrans = values[Matrix.MTRANS_X]
        var yTrans = values[Matrix.MTRANS_Y]
        val xDistance = (event.x - start.x) * DRAG_SPEED_DAMPENER
        val yDistance = (event.y - start.y) * DRAG_SPEED_DAMPENER
        if (imageHelper.getHorizontalMovement(xTrans, xDistance)) xTrans += xDistance
        if (imageHelper.getVerticalMovement(yTrans, yDistance)) yTrans += yDistance

        view.imageMatrix = Matrix().apply {
            this.postScale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y])
            this.postTranslate(xTrans, yTrans)
        }
        imageHelper.update()
    }

    inner class ImageHelper {

        val values = FloatArray(9)
        val minimumScalingValue: Float
        var scaleX = 0f
        var scaleY = 0f

        var imageRect: RectF

        var viewRect: RectF

        var translationX = 0f
            set(value) {
                field = centerOffsetX - value
            }

        var translationY = 0f
            set(value) {
                field = centerOffsetY - value
            }

        var centerOffsetX = 0f
        var centerOffsetY = 0f

        init {
            view.imageMatrix.getValues(values)
            scaleX = values[Matrix.MSCALE_X]
            scaleY = values[Matrix.MSCALE_Y]
            minimumScalingValue = values[Matrix.MSCALE_X]
            imageRect = RectF(
                0f,
                0f,
                view.drawable.intrinsicWidth * scaleX,
                view.drawable.intrinsicHeight * scaleY
            )
            viewRect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
            view.imageMatrix.apply {
                this.setRectToRect(imageRect, viewRect, Matrix.ScaleToFit.CENTER)
            }
            view.imageMatrix.getValues(values)
            update()
        }



        fun update() {
            val values = FloatArray(9)
            view.imageMatrix.getValues(values)
            imageRect = RectF(
                values[Matrix.MTRANS_X],
                values[Matrix.MTRANS_Y],
                values[Matrix.MTRANS_X] + view.drawable.intrinsicWidth * scaleX,
                values[Matrix.MTRANS_Y] + view.drawable.intrinsicHeight * scaleY
            )
            viewRect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
            scaleX = values[Matrix.MSCALE_X]
            scaleY = values[Matrix.MSCALE_Y]
            centerImageInView()
        }

        //check if zoom point is within image bounds
        fun getScaling(point: PointF): PointF {
            var x = point.x
            var y = point.y
            when {
                x < imageRect.left -> x = imageRect.left
                x > imageRect.right -> x = imageRect.right
                y < imageRect.top -> y = imageRect.top
                y > imageRect.bottom -> y = imageRect.bottom
            }
            return PointF(x, y)
        }

        // offset so matrix is centered relative to image view
        fun centerImageInView() {
            centerOffsetX = -(imageRect.width() - viewRect.width()).half()
            centerOffsetY = -(imageRect.height() - viewRect.height()).half()
            translationX = centerOffsetX
            translationY = centerOffsetY
        }


        fun getHorizontalMovement(xTrans: Float, distance: Float): Boolean {
            return if (distance >= 0) {
                canMoveRight(xTrans, distance)
            } else {
                canMoveLeft(xTrans, distance)
            }
        }

        fun getVerticalMovement(yTrans: Float, distance: Float): Boolean {
            return if (distance >= 0) {
                canMoveUp(yTrans, distance)
            } else {
                canMoveDown(yTrans, distance)
            }
        }

        private fun canMoveRight(xTrans: Float, distance: Float): Boolean {
            val dst = xTrans + distance
            return dst < 0
        }

        private fun canMoveLeft(xTrans: Float, distance: Float): Boolean {
            val dst = xTrans + distance
            return dst + imageRect.width() > viewRect.width()
        }

        private fun canMoveUp(yTrans: Float, distance: Float): Boolean {
            val dst = yTrans + distance
            return dst < 0
        }

        private fun canMoveDown(yTrans: Float, distance: Float): Boolean {
            val dst = yTrans + distance
            return dst + imageRect.height() > viewRect.height()
        }


    }

    private fun getNewImageSize(values: FloatArray, factor: Float): SizeF{
        val scaleFactor = values[Matrix.MSCALE_X] * factor
        val startX = values[Matrix.MTRANS_X]
        val startY = values[Matrix.MTRANS_Y]
        val width = view.drawable.intrinsicWidth * scaleFactor
        val height = view.drawable.intrinsicHeight * scaleFactor
        return SizeF(width, height)
    }

    private fun getCenterOffsets(size: SizeF): PointF{
        val x = (view.width - size.width).half()
        val y = (view.height - size.height).half()
        return PointF(x,y)
    }

}
