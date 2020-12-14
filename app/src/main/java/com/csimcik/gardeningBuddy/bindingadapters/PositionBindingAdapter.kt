package com.csimcik.gardeningBuddy.bindingadapters

import android.util.Log
import androidx.databinding.BindingAdapter
import com.csimcik.gardeningBuddy.custom.ui.MeterView

object PositionBindingAdapter {
    const val TAG = "POSITION_BINDING"
    @BindingAdapter("meter:position")
    @JvmStatic
    fun bindPosition(view: MeterView, position: Int) {
        Log.d(TAG, "happening is $position")
        if(position >= 0) {
            view.position = position
        }else{
            view.position = -1
        }
    }
}