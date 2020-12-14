package com.csimcik.gardeningBuddy

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.util.SizeF
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer

const val HALF_OPACITY = 0.5f
const val FULL_OPACITY = 1f

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Float.isDark(): Boolean {
    return this < .5f
}

fun Float.double(): Float {
    return this * 2f
}

fun Float.half(): Float {
    return this * .5f
}

fun AppCompatImageView.desaturate() {
    val colorMatrix = ColorMatrix().apply {
        setSaturation(0f)
        alpha = HALF_OPACITY
    }
    val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
    this.colorFilter = colorMatrixColorFilter
}

fun AppCompatImageView.resaturate() {
    val colorMatrix = ColorMatrix().apply {
        setSaturation(1f)
        alpha = FULL_OPACITY
    }
    val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
    this.colorFilter = colorMatrixColorFilter
}

fun Matrix.getImageSize(view: AppCompatImageView): SizeF {
    val values = FloatArray(9)
    val size =
        SizeF(view.drawable.intrinsicWidth.toFloat(), view.drawable.intrinsicHeight.toFloat())
    this.getValues(values)
    return SizeF(size.width * values[Matrix.MSCALE_X], size.height * values[Matrix.MSCALE_Y])
}

fun GeoJsonLayer.getSelectedFeature(latLng: LatLng): GeoJsonFeature? {
    for (feature in this.features) {
        if (feature.boundingBox.contains(latLng)) return feature
    }
    return null
}