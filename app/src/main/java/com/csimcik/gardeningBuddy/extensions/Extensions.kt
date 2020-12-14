package com.csimcik.gardeningBuddy.extensions

import android.animation.Animator
import android.graphics.PointF
import android.util.Size
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.csimcik.gardeningBuddy.R
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun Float.doubleIt(): Float {
    return this * 2f
}

fun Float.quarter(): Float {
    return this / 4
}

fun Int.half(): Int {
    return this / 2
}

fun Int.halfAsFloat(): Float {
    return this / 2f
}

fun PointF.calculateDistance(prev: PointF): Float {
    return (sqrt((this.x - prev.x).pow(2) + (this.y - prev.y).pow(2)))
}


fun Float.mapTo(inputStart: Float, inputEnd: Float, outputStart: Float, outputEnd: Float): Float {
    return ((this - inputStart) / (inputEnd - inputStart)) * (outputEnd - outputStart) + outputStart
}

fun AppCompatImageView.addRoundedImageNoCrop(url: String, size: Size) {
    Glide.with(this)
        .load(url).placeholder(R.drawable.loading_image_rotate)
        .override(size.width, size.height)
        .error(R.drawable.no_image_grn)
        .transform(
            RoundedCorners(
                this.context.resources.getDimension(R.dimen.family_item_view_radius)
                    .toInt()
            )
        )
        .into(this)
}

fun View.applyScaleAnimation(scaleTo: Float, dur: Long) {
    this.animate().apply {
        scaleX = scaleTo
        scaleY = scaleTo
        duration = dur
        interpolator = AccelerateInterpolator(2.0f)
        setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                scaleX = 1.0f
                scaleY = 1.0f
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
        start()
    }
}

fun Float.normalizeAlphaPercentage(): Int {
    return (255 * this).roundToInt()
}