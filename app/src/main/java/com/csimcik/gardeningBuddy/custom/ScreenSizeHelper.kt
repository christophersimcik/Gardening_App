package com.csimcik.gardeningBuddy.custom

import android.content.Context
import android.os.Build
import android.util.Size
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager

object ScreenSizeHelper {
    fun getScreenSize(context: Context): Size {
        var size: Size? = null
        context.let {
            val windowManager = it.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            size = if (Build.VERSION.SDK_INT >= 30) {
                val windowMetrics = windowManager.currentWindowMetrics
                val insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                val width = windowMetrics.bounds.width() - (insets.left + insets.right)
                val height = windowMetrics.bounds.height() - (insets.top + insets.bottom)
                Size(width, height)
            } else {
                val display = windowManager.defaultDisplay as Display
                val width = display.width
                val height = display.height
                Size(width, height)
            }
        }
        return size ?: Size(0, 0)
    }

    fun getPercentOfHeight(context: Context, percent: Float): Size {
        var size: Size? = null
        context.let {
            val windowManager = it.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            size = if (Build.VERSION.SDK_INT >= 30) {
                val windowMetrics = windowManager.currentWindowMetrics
                val insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                val width = windowMetrics.bounds.width() - (insets.left + insets.right)
                val height = windowMetrics.bounds.height() - (insets.top + insets.bottom)
                Size((width * percent).toInt(), (height * percent).toInt())
            } else {
                val display = windowManager.defaultDisplay as Display
                val width = display.width
                val height = display.height
                Size((width * percent).toInt(), (height * percent).toInt())
            }
        }
        return size ?: Size(0, 0)
    }
}