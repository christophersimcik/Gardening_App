package com.csimcik.gardeningBuddy.custom.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView

class DisappearingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "DISAPPEAR_TXT_VIEW"
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        val state = if (screenState == SCREEN_STATE_OFF) "off" else "on"
        Log.d(TAG, state)
    }
}