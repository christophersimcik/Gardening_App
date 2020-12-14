package com.csimcik.gardeningBuddy.bindingadapters

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.csimcik.gardeningBuddy.R

object PersistenceBindingAdapter {
    @JvmStatic
    @androidx.databinding.BindingAdapter("app:persistence")
    fun setText(view: TextView, persistence: Boolean?) {
        when (persistence) {
            true -> {
                if(view.id == R.id.retention_foliage){
                    view.text = view.context.getString(R.string.evergreen)
                }else{
                    view.text = view.context.getString(R.string.persistent)
                }
                view.setTextColor(ContextCompat.getColor(view.context, R.color.default_text))
                view.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            }
            false -> {
                view.text = view.context.getString(R.string.deciduous)
                view.setTextColor(ContextCompat.getColor(view.context, R.color.default_text))
                view.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            }
            null -> {
                view.text = view.context.getString(R.string.unknown)
                view.setTextColor(ContextCompat.getColor(view.context, R.color.mid_gray))
                view.setTypeface(Typeface.DEFAULT, Typeface.ITALIC)
            }
        }
    }
}