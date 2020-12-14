package com.csimcik.gardeningBuddy.custom.ui

import android.view.View
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.extensions.half

class GalleryItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = spacing
        outRect.right = spacing.half()
        outRect.left = spacing.half()
        outRect.top = 0
    }
}