package com.csimcik.gardeningBuddy.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.databinding.GalleryItemBinding
import com.csimcik.gardeningBuddy.models.plantDetail.Image

class GalleryAdapter(val data: List<Image?>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    lateinit var listener: GalleryItemCallback

    companion object {
        const val TAG = "GALLERY_ADAPTER"
        const val ITEM_SPACING = 40
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        val binding = getGalleryItemBinding(parent)
        return ViewHolder(binding, binding.root)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        Log.d(TAG, " url = ${data[position]?.image_url}")
        holder.populateImage(data[position]?.image_url ?: "")
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(binding: GalleryItemBinding, itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val image: AppCompatImageView = binding.image
        private var url: String = ""

        init {
            image.setOnClickListener(this)
        }

        fun populateImage(url: String) {
            this.url = url
            Glide.with(image)
                .load(url).placeholder(R.drawable.loading_image_rotate)
                .error(R.drawable.no_image_grn)
                .transform(CenterCrop(), RoundedCorners(image.context.resources.getDimension(R.dimen.family_item_view_radius).toInt()))
                .into(image)
        }

        override fun onClick(p0: View?) {
            Log.d(TAG, "clicked")
            if (this@GalleryAdapter::listener.isInitialized) listener.itemClicked(
                this.url,
                getViewCoordinates()
            )
        }

        private fun getViewCoordinates(): ViewData {
            val location = IntArray(2)
            itemView.getLocationInWindow(location)
            return ViewData(location[0], location[1])
        }

        inner class ViewData(val x: Int, val y: Int)

    }

    private fun getGalleryItemBinding(parent: ViewGroup): GalleryItemBinding {
        return GalleryItemBinding.inflate(LayoutInflater.from(parent.context))
    }

    interface GalleryItemCallback {
        fun itemClicked(url: String, viewData: ViewHolder.ViewData)
    }

}