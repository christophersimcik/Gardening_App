package com.csimcik.gardeningBuddy.fragments

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.CycleInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.custom.ui.AutoSizeEditTextView
import com.csimcik.gardeningBuddy.databinding.QueryFragmentBinding
import com.csimcik.gardeningBuddy.viewModels.SearchViewModel

class SearchFragment : Fragment(), AutoSizeEditTextView.TextChangedCallback {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var motionLayout: MotionLayout
    private lateinit var binding: QueryFragmentBinding
    private lateinit var inputField: AutoSizeEditTextView
    private lateinit var familyButton: AppCompatImageView
    private lateinit var searchButton: AppCompatImageView
    private lateinit var distributionButton: AppCompatImageView


    private var previousRootYValue = 0.0f

    companion object {
        const val TAG = "SEARCH_FRAGMENT"
        const val DELAY = 500L
        const val ANIMATION_DURATION = 400L
        const val ANIMATION_CYCLES = 1.0f
        const val ANIMATION_SCALE_FACTOR = 1.20f
        const val DECELERATION_INTERPOLATOR_FACTOR = 2.0f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QueryFragmentBinding.inflate(inflater, container, false)
        bindViews(binding)
        initButtons(arrayOf(distributionButton, familyButton, searchButton))
        attachFocusListener(inputField)
        addOnGlobalLayoutChangedListener(binding)
        inputField.addTextChangedListener(inputField)
        inputField.registerListener(this)
        inputField.setText(viewModel.search)
        inputField.setOnEditorActionListener { _, int, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || int == EditorInfo.IME_ACTION_DONE) {
                Handler(Looper.getMainLooper()).postDelayed({ searchPlants(inputField) }, DELAY)
            }
            false
        }
        showButtons(arrayOf(familyButton, distributionButton))
        return binding.root
    }


    override fun onTextChanged(string: String) {
        viewModel.search = string
    }

    private fun bindViews(binding: QueryFragmentBinding) {
        inputField = binding.editText
        familyButton = binding.searchFamily
        searchButton = binding.searchButton
        motionLayout = binding.searchMotionLayout
        distributionButton = binding.searchDistribution

    }

    override fun onStop() {
        super.onStop()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener { this }
    }

    private fun addOnGlobalLayoutChangedListener(binding: QueryFragmentBinding) {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            toggleInputFocus(inputField.height.toFloat() <= previousRootYValue)
            previousRootYValue = inputField.height.toFloat()
        }
    }

    private fun attachFocusListener(inputField: AutoSizeEditTextView) {
        inputField.setOnFocusChangeListener { _, boolean ->
            if (boolean) {
                hideButtons(arrayOf(familyButton, distributionButton))
            } else {
                showButtons(arrayOf(familyButton, distributionButton))
            }
        }
    }

    private fun toggleInputFocus(keyboardIsVisible: Boolean) {
        Log.d(TAG, "BOOLEAN = $keyboardIsVisible")
        if (!keyboardIsVisible and this::inputField.isInitialized) inputField.clearFocus()
    }

    private fun initButtons(views: Array<AppCompatImageView>) {
        views.forEach { view ->
            view.setOnClickListener { animateButton(view) }
        }
    }

    private fun hideButtons(views: Array<AppCompatImageView>) {
        Log.d(TAG, "**hide")
        views.forEach { view ->
            view.animate()
                .setDuration(DELAY)
                .translationY(binding.root.height.toFloat())
                .setInterpolator(DecelerateInterpolator(DECELERATION_INTERPOLATOR_FACTOR))
                .start()
        }
        motionLayout.transitionToEnd()
    }

    private fun showButtons(views: Array<AppCompatImageView>) {
        Log.d(TAG, "**show")
        views.forEach { view ->
            view.animate()
                .setDuration(DELAY)
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator(DECELERATION_INTERPOLATOR_FACTOR))
                .start()
        }
        motionLayout.transitionToStart()
    }


    private fun animateButton(view: View) {
        view.animate()
            .setInterpolator(CycleInterpolator(ANIMATION_CYCLES))
            .setDuration(ANIMATION_DURATION)
            .scaleX(ANIMATION_SCALE_FACTOR)
            .scaleY(ANIMATION_SCALE_FACTOR)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    when (view) {
                        distributionButton -> searchMap(view)
                        familyButton -> searchFamilies(view)
                        searchButton -> {
                            searchPlants(view)
                            dismissKeyBoard()
                        }
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            })
            .start()
    }

    private fun searchMap(view: View) {
        Navigation.findNavController(view).navigate(R.id.mapFragment)
    }

    private fun searchFamilies(view: View) {
        Navigation.findNavController(view).navigate(R.id.familiesFragment)
    }

    private fun searchPlants(view: View) {
        val bundle = Bundle().also { it.putString(PlantsFragment.SEARCH, viewModel.search) }
        Navigation.findNavController(view).navigate(R.id.plantsFragment, bundle)
    }

    private fun dismissKeyBoard(){
        context?.let{
           val inputMethodManager =  it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken,0)

        }
    }

}