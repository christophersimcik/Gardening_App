package com.csimcik.gardeningBuddy.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.databinding.MapDialogBinding

class MapDialog() : DialogFragment() {
    companion object {
        const val TAG = "MAP DIALOG"
        const val COUNTRY_NAME = "COUNTRY_NAME"
        const val MIN_SCALE = 1f
        const val MAX_SCALE = 1.25f
        const val DURATION = 250L
        const val ACCELERATE_FACTOR = 2.0f
    }

    private lateinit var binding: MapDialogBinding
    private lateinit var search: AppCompatTextView
    private lateinit var confirmButton: AppCompatImageButton
    private lateinit var cancelButton: AppCompatImageButton
    private lateinit var listener: DialogCallback
    private var selectedCountryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCountryName = arguments?.getString(COUNTRY_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MapDialogBinding.inflate(inflater, container, false)
        initiateViews(binding)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.let { it ->
            val size = getScreenSize()
            val width = size.width
            val height = size.height
            it.setLayout(width, height)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        if(this::listener.isInitialized) listener.onCanceled(this.dialog)
    }

    override fun setTargetFragment(fragment: Fragment?, requestCode: Int) {
        listener = fragment as DialogCallback
        super.setTargetFragment(fragment, requestCode)
    }

    fun setListener(listener: DialogCallback){
        this.listener = listener
    }

    private fun initiateViews(binding: MapDialogBinding) {
        search = binding.searchText.apply {
            text = context.resources.getString(R.string.map_search, selectedCountryName)
        }
        confirmButton = binding.confirmButton.apply {
            setOnClickListener {
                animate(it as AppCompatImageButton)
            }
        }
        cancelButton = binding.cancelButton.apply {
            setOnClickListener {
                animate(it as AppCompatImageButton)
            }
        }
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

    private fun animate(button: AppCompatImageButton) {
        val otherButton = when {
            button == confirmButton -> cancelButton
            button == cancelButton -> confirmButton
            else -> confirmButton
        }
        ValueAnimator.ofFloat(1.0f, 0.2f).apply {
            duration = DURATION
            interpolator = DecelerateInterpolator(ACCELERATE_FACTOR)
            addUpdateListener { otherButton.alpha = (it.animatedValue as Float) }
            start()
        }
        ValueAnimator.ofFloat(MIN_SCALE, MAX_SCALE).apply {
            duration = DURATION
            interpolator = DecelerateInterpolator(ACCELERATE_FACTOR)
            addUpdateListener { valueAnimator ->
                (valueAnimator.animatedValue as Float).apply {
                    button.scaleX = this
                    button.scaleY = this
                }
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    when (button) {
                        confirmButton -> if (this@MapDialog::listener.isInitialized) listener.onConfirmed(dialog)
                        cancelButton -> if (this@MapDialog::listener.isInitialized) listener.onDismissed(dialog)
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            })
            start()
        }
    }

    interface DialogCallback {
        fun onConfirmed(dialog: Dialog?)
        fun onDismissed(dialog: Dialog?)
        fun onCanceled(dialog: Dialog?)
    }
}

