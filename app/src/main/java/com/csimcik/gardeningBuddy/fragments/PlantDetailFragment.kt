package com.csimcik.gardeningBuddy.fragments

import android.animation.Animator
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.SHARED_PREFERENCES
import com.csimcik.gardeningBuddy.adapters.PlantAdapter
import com.csimcik.gardeningBuddy.databinding.FragementDetailPlantNewBinding
import com.csimcik.gardeningBuddy.dialog.GalleryDialog
import com.csimcik.gardeningBuddy.enums.ImageType
import com.csimcik.gardeningBuddy.models.plantDetail.Image
import com.csimcik.gardeningBuddy.models.plantDetail.PlantDetail
import com.csimcik.gardeningBuddy.custom.ui.ColorsView
import com.csimcik.gardeningBuddy.custom.ui.MeterView
import com.csimcik.gardeningBuddy.viewModels.PlantDetailViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import kotlin.random.Random

class PlantDetailFragment :
    Fragment(),
    OnMapReadyCallback,
    ColorsView.ColorTouchCallback,
    PlantDetailViewModel.FeaturesCompiledCallback,
    View.OnClickListener {
    private lateinit var googleMap: MapView
    private lateinit var textView: TextView
    private lateinit var frameLayout: FrameLayout
    private lateinit var thumb: AppCompatImageView
    private lateinit var geoJsonLayer: GeoJsonLayer
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var observer: Observer<PlantDetail>
    private lateinit var humidityMeter: MeterView
    private lateinit var lightMeter: MeterView
    private lateinit var nativeButton: AppCompatRadioButton
    private lateinit var synonyms: AppCompatTextView
    private lateinit var flowerImageButton: AppCompatImageView
    private lateinit var foliageImageButton: AppCompatImageView
    private lateinit var fruitAndSeedImageButton: AppCompatImageView
    private lateinit var moreInfoButton: AppCompatCheckBox
    private lateinit var introducedButton: AppCompatRadioButton
    private lateinit var loadingScreen: ConstraintLayout
    private lateinit var binding: FragementDetailPlantNewBinding
    private lateinit var galleryDialog: GalleryDialog
    private var timestamp: Long = 0
    private val viewModel: PlantDetailViewModel by viewModels()
    private var colors = ArrayList<ColorsView>()
    private var activeColorView: ColorsView? = null
    private var locationAsArray = IntArray(2)

    companion object {
        const val TAG = "PLANT_DETAIL_FRAGMENT"
        val dummyList = arrayListOf(
            "red",
            "blue",
            "black",
            "light green",
            "Light Blue",
            "gold",
            "fuchsia",
            "yellow",
            "sienna",
            "teal",
            "plum",
            "ivory",
            "cream"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.listener = this
        textView = TextView(this.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(25, 5, 25, 10)
            background = ContextCompat.getDrawable(context, R.drawable.rounded_background)
            textSize = 18f
            gravity = Gravity.CENTER
            elevation = 2f
            setTypeface(typeface, Typeface.BOLD)
            visibility = View.GONE
        }
        viewModel.plant = arguments?.getString(PlantAdapter.ID, "")
        viewModel.plantName = arguments?.getString(PlantAdapter.NAME, "")
        context?.let {
            sharedPreferences = it.getSharedPreferences(SHARED_PREFERENCES, 0)
        }
        Log.d(TAG, "CREATE")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ON VIEW CREATED")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ON CREATE VIEW at")
        binding = FragementDetailPlantNewBinding.inflate(inflater, container, false)
        loadingScreen = binding.plantDetailLoadingScreen.loadingContainer.apply {
            background = ContextCompat.getDrawable(this.context, R.drawable.loading_background)
        }
        googleMap = binding.distributionCompartment.map
        thumb = binding.namesCompartment.plantThumbnail
        flowerImageButton =
            binding.flowerCompartment.imagesIcon.also { it.setOnClickListener(this) }
        foliageImageButton =
            binding.foliageCompartment.imagesIcon.also { it.setOnClickListener(this) }
        fruitAndSeedImageButton =
            binding.fruitAndSeedCompartment.imagesIcon.also { it.setOnClickListener(this) }
        nativeButton = binding.distributionCompartment.nativeRadioButton
        nativeButton.setOnClickListener {
            viewModel.setDistributionSetting(
                sharedPreferences,
                PlantDetailViewModel.NATIVE
            )
            Log.d(TAG, "native clicked")
            viewModel.checkDistributionData(PlantDetailViewModel.NATIVE)
            activateDistributionZones()
        }
        synonyms = binding.namesCompartment.synonyms
        moreInfoButton = binding.namesCompartment.moreInfoButton
        moreInfoButton.setOnClickListener {
            it as AppCompatCheckBox
            if (it.isChecked) {
                synonyms.visibility = View.VISIBLE
            } else {
                synonyms.visibility = View.GONE
            }
        }
        introducedButton = binding.distributionCompartment.introducedRadioButton
        introducedButton.setOnClickListener {
            viewModel.setDistributionSetting(
                sharedPreferences,
                PlantDetailViewModel.INTRODUCED
            )
            Log.d(TAG, "intro clicked")
            viewModel.checkDistributionData(PlantDetailViewModel.INTRODUCED)
            activateDistributionZones()
        }
        humidityMeter = binding.humidityCompartment.humidityMeter
        lightMeter = binding.lightCompartment.lightMeter
        colors.add(binding.flowerCompartment.flowerColorView)
        colors.add(binding.foliageCompartment.foliageColorView)
        colors.add(binding.fruitAndSeedCompartment.fruitAndSeedColorView)
        colors.forEach {
            it.setOnTouchListener { view, _ ->
                view.performClick()
            }
            it.listener = this
            populateColorsWithDummyData()
        }
        googleMap.getMapAsync(this)
        googleMap.onCreate(savedInstanceState)
        googleMap.onResume()
        frameLayout = binding.rootLayout
        frameLayout.addView(textView)
        binding.rootView.viewTreeObserver.addOnScrollChangedListener {
            textView.animate()
                .y(locationAsArray[1].toFloat() - 375)
                .setDuration(0)
                .start()
            humidityMeter.isOnScreen = isViewVisible(humidityMeter)
            lightMeter.isOnScreen = isViewVisible(lightMeter)

        }
        observer = Observer { plant ->
            loadingScreen.visibility = View.GONE
            addThumb(plant.mainSpecies?.image_url ?: "")
            binding.plant = plant
            viewModel.plantDetail = plant
            viewModel.setGeoJsonFeatures(geoJsonLayer)
        }
        viewModel.getPlant().observe(viewLifecycleOwner, observer)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.plantName = viewModel.plantName
        return binding.rootLayout
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ON START at ${System.currentTimeMillis() - timestamp}")
        if (this::googleMap.isInitialized) googleMap.onStart()
        timestamp = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ON RESUME at ${System.currentTimeMillis() - timestamp}")
        timestamp = System.currentTimeMillis()
        humidityMeter.startAnimation()
        lightMeter.startAnimation()
        if (this::googleMap.isInitialized) googleMap.onStart()
    }

    override fun onPause() {
        super.onPause()
        if (this::googleMap.isInitialized) googleMap.onPause()
    }

    override fun onStop() {
        super.onStop()
        //make sure child is orphan
        frameLayout.removeView(textView)
        if (this::googleMap.isInitialized) googleMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.listener = null
        if (this::googleMap.isInitialized) googleMap.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::googleMap.isInitialized) googleMap.onSaveInstanceState(
            outState
        )
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (this::googleMap.isInitialized) googleMap.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            Log.d(TAG, "map ready")
            getMapStyleOptions(it)
            it.uiSettings.isMapToolbarEnabled = false
            it.setPadding(50, 0, 0, 50)
            addGeoJsonToMap(it)
            map.clear()
        }
    }

    private fun getMapStyleOptions(googleMap: GoogleMap) {
        try {
            val success: Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    override fun onMove(view: View, color: String) {
        Log.d(TAG, "move")
        textView.x = (view.width / 2f) - (textView.width / 2f)
        textView.text = color
    }

    override fun onDown(view: View, color: String) {
        Log.d(TAG, "down")
        textView.text = color
        textView.x = ((this.view?.width ?: 0) / 2f) - (textView.width / 2f)
        textView.visibility = View.VISIBLE
        activeColorView = view as ColorsView
        activeColorView?.getLocationOnScreen(locationAsArray)
        textView.animate()
            .y(locationAsArray[1].toFloat() - 375)
            .setDuration(0)
            .start()
    }

    override fun onUp(view: View) {
        Log.d(TAG, "UP")
        Handler(Looper.getMainLooper()).postDelayed({ textView.visibility = View.GONE }, 250)
        activeColorView = null
    }

    override fun onCancel(view: View) {
        Log.d(TAG, "CANCEL")
        Handler(Looper.getMainLooper()).postDelayed({ textView.visibility = View.GONE }, 250)
        activeColorView = null
    }

    private fun populateColorsWithDummyData() {
        for (color in colors) {
            val temp = ArrayList<String>()
            for (i in 0..Random.nextInt(3, 7)) {
                temp.add(dummyList[Random.nextInt(dummyList.lastIndex)])
            }
            color.listOfColors = temp
        }
    }

    private fun addThumb(url: String) {
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.loading_image_rotate)
            .error(R.drawable.no_image_grn)
            .transform(
                CircleCrop(),
                CenterCrop(),
            )
            .into(thumb)
    }

    private fun createGeoJsonLayers(map: GoogleMap) {
        geoJsonLayer = GeoJsonLayer(map, R.raw.low_poly_data_coordinates, this.context).apply {
            defaultPolygonStyle.strokeWidth = 0f
            defaultPolygonStyle.fillColor = Color.TRANSPARENT
        }
    }

    private fun addGeoJsonToMap(map: GoogleMap) {
        createGeoJsonLayers(map)
        geoJsonLayer.addLayerToMap()
    }


    private fun activateDistributionZones() {
        val activeStyle = getActivePolygonStyle()
        val defaultStyle = getDefaultPolygonStyle()
        if (viewModel.distributionSettings == PlantDetailViewModel.NATIVE) {
            viewModel.nativeGeoJsonFeatures.forEach {
                it.polygonStyle = activeStyle
            }
            viewModel.introducedGeoJsonFeatures.forEach {
                it.polygonStyle = defaultStyle
            }
            Log.d(TAG, "intro-sz = ${viewModel.introducedGeoJsonFeatures}")
            Log.d(TAG, "nativ-sz = ${viewModel.nativeGeoJsonFeatures}")
        } else {
            viewModel.nativeGeoJsonFeatures.forEach {
                it.polygonStyle = defaultStyle
            }
            viewModel.introducedGeoJsonFeatures.forEach {
                it.polygonStyle = activeStyle
            }
        }
    }

    private fun getDefaultPolygonStyle(): GeoJsonPolygonStyle {
        val defaultPolygonColor = context?.let {
            ContextCompat.getColor(it, R.color.custom_google_map_gray)
        } ?: Color.LTGRAY
        val defaultPolygonStyle = GeoJsonPolygonStyle()
        defaultPolygonStyle.strokeWidth = 0f
        defaultPolygonStyle.fillColor = defaultPolygonColor
        return defaultPolygonStyle
    }

    private fun getActivePolygonStyle(): GeoJsonPolygonStyle {
        val stroke = context?.let {
            ContextCompat.getColor(it, R.color.default_text)
        } ?: Color.DKGRAY
        val fill = context?.let {
            ContextCompat.getColor(it, R.color.light_gray)
        } ?: Color.RED
        val activePolygonStyle = GeoJsonPolygonStyle()
        activePolygonStyle.fillColor = fill
        activePolygonStyle.strokeColor = stroke
        activePolygonStyle.strokeWidth = 3f
        return activePolygonStyle
    }

    override fun onFeaturesCompiled() {
        activateDistributionZones()
        viewModel.checkDistributionData(PlantDetailViewModel.NATIVE)
    }

    private fun isViewVisible(view: View): Boolean {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        return rect.height() != view.height
    }

    override fun onClick(view: View?) {
        view?.let { pic -> scaleUp(pic) }
    }

    private fun getListOfImages(imageType: ImageType): List<Image?> {
        var list: List<Image?>? = null
        val images = viewModel.plantDetail?.let { plant ->
            plant.mainSpecies?.images
        }
        images?.let {
            when (imageType) {
                ImageType.FLOWER -> list = images.flower
                ImageType.FOLIAGE -> list = images.leaf
                ImageType.FRUIT -> list = images.fruit
                ImageType.BARK -> list = images.bark
                ImageType.PLANT -> Log.d(TAG, "add plants")
            }
        }
        return list ?: emptyList()
    }

    private fun scaleUp(view: View) {
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(75)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    scaleDown(view)
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            }).start()
    }

    private fun scaleDown(view: View) {
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(75)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    val fragmentManager = parentFragmentManager
                    when (view) {
                        flowerImageButton -> {
                            val type = ImageType.FLOWER
                            galleryDialog = GalleryDialog(type, getListOfImages(type))
                            galleryDialog.setTargetFragment(this@PlantDetailFragment, 0)
                            galleryDialog.show(fragmentManager, "")
                        }
                        foliageImageButton -> {
                            val type = ImageType.FOLIAGE
                            galleryDialog = GalleryDialog(type, getListOfImages(type))
                            galleryDialog.setTargetFragment(this@PlantDetailFragment, 0)
                            galleryDialog.show(fragmentManager, "")
                        }
                        fruitAndSeedImageButton -> {
                            val type = ImageType.FRUIT
                            galleryDialog = GalleryDialog(type, getListOfImages(type))
                            galleryDialog.setTargetFragment(this@PlantDetailFragment, 0)
                            galleryDialog.show(fragmentManager, "")
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
}

