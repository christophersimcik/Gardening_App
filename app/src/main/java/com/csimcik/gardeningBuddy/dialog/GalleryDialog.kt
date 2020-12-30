package com.csimcik.gardeningBuddy.dialog

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.view.animation.CycleInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.adapters.GalleryAdapter
import com.csimcik.gardeningBuddy.databinding.GalleryDialogBinding
import com.csimcik.gardeningBuddy.enums.ImageType
import com.csimcik.gardeningBuddy.extensions.addRoundedImageNoCrop
import com.csimcik.gardeningBuddy.models.plantDetail.Image
import com.csimcik.gardeningBuddy.custom.ui.GalleryItemDecoration
import com.csimcik.gardeningBuddy.models.plantDetail.Images
import com.github.chrisbanes.photoview.PhotoView


class GalleryDialog() : DialogFragment(),
    GalleryAdapter.GalleryItemCallback {

    companion object {
        const val TAG = "GALLERY DIALOG"
        const val IMAGE_TYPE = "IMAGE_TYPE"
        const val IMAGES = "IMAGES"
    }

    private lateinit var cancel_button: AppCompatImageView
    private lateinit var binding: GalleryDialogBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var motionlayout: MotionLayout
    private lateinit var header: AppCompatTextView
    private lateinit var msg: AppCompatTextView
    private lateinit var adapter: GalleryAdapter
    private lateinit var detail: PhotoView
    private lateinit var type: ImageType
    private var data: Images? = null
    private lateinit var images: List<Image?>

    private val decoration = GalleryItemDecoration(GalleryAdapter.ITEM_SPACING)


    private var imagePoint = PointF()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getSerializable(IMAGE_TYPE) as ImageType
        data = arguments?.getParcelable(IMAGES) as Images?
        images = when (type) {
            ImageType.PLANT -> data?.habit ?: ArrayList<Image>()
            ImageType.FOLIAGE -> data?.leaf ?: ArrayList<Image>()
            ImageType.FLOWER -> data?.flower ?: ArrayList<Image>()
            ImageType.FRUIT -> data?.fruit ?: ArrayList<Image>()
            ImageType.BARK -> data?.bark ?: ArrayList<Image>()
        }
        adapter = GalleryAdapter(images)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GalleryDialogBinding.inflate(inflater, container, false)
        initiateViews(binding)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dialogWindow = dialog?.window
        dialogWindow?.let { window ->
            val size = getScreenSize()
            val width = (size.width * .90).toInt()
            val height = (size.height * .90).toInt()
            window.setLayout(width, height)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        Log.d(TAG, "pad right = $recyclerView")
        Log.d(TAG, "pad right = ${recyclerView.layoutManager?.paddingEnd}")
    }

    private fun initiateViews(binding: GalleryDialogBinding) {
        binding.type = type
        recyclerView = binding.galleryRecyclerView
        setUpRecyclerView()
        header = binding.header
        detail = binding.galleryDetail
        msg = binding.noImagesText
        if (images.isNotEmpty()) msg.visibility = View.GONE
        motionlayout = binding.galleryMotionLayout
        cancel_button = binding.cancelButton.also {
            it.setOnClickListener { view ->
                scale(view)
            }
        }
    }

    private fun setUpRecyclerView() {
        recyclerView.adapter = adapter
        adapter.listener = this
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, determineSpan())
        recyclerView.addItemDecoration(decoration)
    }

    private fun determineSpan(): Int {
        return if (images.size > 1) 2 else 1
    }

    private fun getScreenSize(): Size {
        var size: Size? = null
        context?.let {
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
        return size ?: Size(500, 1000)
    }

    override fun itemClicked(url: String, viewData: GalleryAdapter.ViewHolder.ViewData) {
        detail.x = (viewData.x).toFloat()
        detail.y = (viewData.y).toFloat()
        imagePoint = PointF(viewData.x.toFloat(), viewData.y.toFloat())
        detail.addRoundedImageNoCrop(url, Size(recyclerView.width, recyclerView.height))
        motionlayout.transitionToState(R.id.enlarge)
        detail.animate()
            .setUpdateListener { fitImageIntoView(detail) }
            .setDuration(500)
            .translationX(0f)
            .translationY(0f)
            .start()
        detail.visibility = View.VISIBLE
    }

    private fun fitImageIntoView(view: AppCompatImageView) {
        val img = view.drawable
        val imgRect = RectF(0f, 0f, img.intrinsicWidth.toFloat(), img.intrinsicHeight.toFloat())
        val viewRect = RectF(0f, 0f, detail.width.toFloat(), detail.height.toFloat())
        view.imageMatrix = Matrix().apply {
            this.setRectToRect(imgRect, viewRect, Matrix.ScaleToFit.CENTER)
        }
    }

    private fun scale(view: View) {
        view.animate().apply {
            scaleX(1.20f)
            scaleY(1.20f)
            duration = 300
            interpolator = CycleInterpolator(1f)
            setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (detail.visibility == View.INVISIBLE) {
                        dialog?.dismiss()
                    } else {
                        motionlayout.transitionToStart()
                        Log.d(TAG, "X = ${imagePoint.x} and Y = ${imagePoint.y}")
                        detail.animate()
                            .setDuration(500)
                            .x(imagePoint.x)
                            .y(imagePoint.y)
                            .start()
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }
            })
        }
    }
}