package com.csimcik.gardeningBuddy.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R
import kotlin.random.Random

object ColorHelper {

    fun getDrawables(context: Context, color: Int): Array<Drawable> {

        val activeColor = ContextCompat.getColor(context,R.color.light_gray)

        // create a 2d array with state and its opposite -state
        val states = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_pressed)
        )

        // create array of corresponding colors
        val colors = intArrayOf(activeColor, color)

        // create color state list
        val colorStateList = ColorStateList(states, colors)

        val top = getTopDrawable(context, colorStateList)
        val bottom = getBottomDrawable(context, colorStateList)
        return arrayOf(top, bottom)
    }

    private fun getTopDrawable(context: Context, color: ColorStateList): Drawable {

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            val radius = context.resources.getDimension(R.dimen.family_item_view_radius)
            cornerRadii = floatArrayOf(
                radius,
                radius,
                radius,
                radius,
                0f,
                0f,
                0f,
                0f
            )
        }
    }

    private fun getBottomDrawable(context: Context, color: ColorStateList): Drawable {

        val strokeWidth = context.resources.getInteger(R.integer.stroke_width_10)
        val radius = context.resources.getDimension(R.dimen.family_item_view_radius)
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(strokeWidth, color)
            cornerRadii = floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                radius,
                radius,
                radius,
                radius
            )
        }
    }

    fun getGreen(): Int {
        val red = Random.nextInt(50, 256)
        val green = Random.nextInt(225, 256)
        val blue = Random.nextInt(50, 151)
        val hsv = FloatArray(3)
        Color.RGBToHSV(red, green, blue, hsv)
        hsv[2] = Random.nextInt(50, 91) / 100f
        hsv[1] = Random.nextInt(5, 101) / 100f
        return Color.HSVToColor(hsv)
    }
}