package com.csimcik.gardeningBuddy.bindingadapters

import android.view.View
import android.widget.TextView

object MapTextBindingAdapter {
    @JvmStatic
    @androidx.databinding.BindingAdapter("app:map_text")
    fun setText(view: TextView, hasData: Boolean? ) {
        when (hasData) {
            true -> view.visibility = View.GONE
            false -> view.visibility = View.VISIBLE
            null -> view.visibility = View.VISIBLE
        }
    }
}