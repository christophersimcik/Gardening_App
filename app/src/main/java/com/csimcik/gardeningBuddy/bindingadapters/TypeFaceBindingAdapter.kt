package com.csimcik.gardeningBuddy.bindingadapters

import android.graphics.Typeface
import android.widget.TextView


object TypeFaceBindingAdapter {
    @JvmStatic
    @androidx.databinding.BindingAdapter("android:style")
    fun setTypeface(view: TextView, style: String?) {
        when (style) {
            "bold" -> view.setTypeface(null, Typeface.BOLD)
            "italic" -> view.setTypeface(null, Typeface.ITALIC)
            "normal" -> view.setTypeface(null, Typeface.NORMAL)
            else -> view.setTypeface(null, Typeface.NORMAL)
        }
    }
}