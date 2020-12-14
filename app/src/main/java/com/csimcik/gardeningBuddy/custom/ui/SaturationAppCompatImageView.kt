package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.util.AttributeSet
import com.csimcik.gardeningBuddy.desaturate
import com.csimcik.gardeningBuddy.resaturate
import androidx.appcompat.widget.AppCompatImageView

class SaturationAppCompatImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun setPressed(pressed: Boolean) {
        if (pressed) this.desaturate() else this.resaturate()
        super.setPressed(pressed)
    }
}