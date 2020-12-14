package com.csimcik.gardeningBuddy.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.SHARED_PREFERENCES
import com.csimcik.gardeningBuddy.custom.PermissionHelper
import com.csimcik.gardeningBuddy.databinding.QueryMapFragmentBinding
import com.csimcik.gardeningBuddy.dialog.MapDialog
import com.csimcik.gardeningBuddy.extensions.applyScaleAnimation
import com.csimcik.gardeningBuddy.models.entities.Country
import com.csimcik.gardeningBuddy.custom.ui.MapOverlay
import com.csimcik.gardeningBuddy.viewModels.MapViewModel
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

class MapFragment : Fragment(), OnMapReadyCallback, MapDialog.DialogCallback,
    GoogleMap.OnMapClickListener {

    companion object {
        const val TAG = "MAP_FRAGMENT"
        const val MAP_STYLE_ERROR = "ERROR_APPLYING_STYLE"
        const val MAP_STYLE_NOT_FOUND_ERROR = "STYLE_NOT_FOUND"
        const val ZOOM_LVL = 3.0f
        const val BUTTON_SCALE_FACTOR = 1.10f
        const val BUTTON_DURATION = 150L
        const val FAB_DURATION = 500L
    }

    private val viewModel: MapViewModel by viewModels()

    private lateinit var dialog: MapDialog
    private lateinit var overlay: MapOverlay
    private var googleMap: GoogleMap? = null
    private lateinit var googleMapView: MapView
    private lateinit var binding: QueryMapFragmentBinding
    private lateinit var motionLayout: MotionLayout
    private lateinit var locationButton: AppCompatImageView
    private lateinit var homeMarker: Marker
    private lateinit var lastMarker: Marker

    private var canClick = true
    private var clicked = false

    private val selectionObserver = Observer<Country> { country ->
        displayPolygons(context?.let { ContextCompat.getColor(it, R.color.default_text) }
            ?: Color.RED)
        if (clicked) showDialog(country)
        Log.d(TAG, "Observed")
        canClick = false
        clicked = false
    }

    private val observingDatabase = Observer<List<Country>> { geography ->
        viewModel.countries = geography
    }

    private val homeObserver = Observer<LatLng> { latLng ->
        addHomeMarker(latLng)
        showFab()
    }

    private val locationObserver = Observer<LatLng> { latLng ->
        relocate(latLng)
        showFab()
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCountries().observe(this, observingDatabase)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QueryMapFragmentBinding.inflate(inflater, container, false)
        initViews(binding)
        initLocationUtils()
        initGoogleMap(savedInstanceState)
        viewModel.locationLiveData.observe(viewLifecycleOwner, locationObserver)
        viewModel.homeLiveData.observe(viewLifecycleOwner, homeObserver)
        viewModel.country.observe(viewLifecycleOwner, selectionObserver)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        googleMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        canClick = true
        googleMapView.onResume()
    }

    override fun onPause() {
        canClick = false
        super.onPause()
        googleMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        googleMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        googleMapView.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        googleMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        googleMapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            this.googleMap = it
            getMapStyleOptions(it)
            it.uiSettings.isMapToolbarEnabled = false
            googleMap?.setOnCameraIdleListener { showFab() }
            googleMap?.setOnCameraMoveStartedListener { hideFab() }
            googleMap?.setOnMapClickListener(this)
            getHome()
            setLocation()
        }
    }

    private fun getMapStyleOptions(googleMap: GoogleMap) {
        try {
            val success: Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
            if (!success) {
                Log.e(PlantDetailFragment.TAG, MAP_STYLE_ERROR)
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(PlantDetailFragment.TAG, MAP_STYLE_NOT_FOUND_ERROR, e)
        }
    }

    private fun addHomeMarker(latLng: LatLng) {
        googleMap?.let {
            homeMarker = it.addMarker(
                MarkerOptions()
                    .title(context?.getString(R.string.you) ?: "")
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            )
        }
    }

    private fun initViews(binding: QueryMapFragmentBinding) {
        locationButton = binding.mapHomeFab.apply {
            setOnClickListener {
                centerMapOnHome()
                this.applyScaleAnimation(BUTTON_SCALE_FACTOR, BUTTON_DURATION)
            }
        }
        motionLayout = binding.background
        googleMapView = binding.mapView
        overlay = binding.mapOverlay
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        googleMapView.getMapAsync(this)
        googleMapView.onCreate(savedInstanceState)
    }

    override fun onConfirmed() {
        this.dialog.dismiss()
        canClick = true
        removePolygons(viewModel.country.value?.getPolygons())
        search()
    }

    override fun onCanceled() {
        this.dialog.dismiss()
        canClick = true
        removePolygons(viewModel.country.value?.getPolygons())
        showFab()
    }

    override fun onDismissed() {
        this.dialog.dismiss()
        canClick = true
        removePolygons(viewModel.country.value?.getPolygons())
        showFab()
    }

    private fun search() {
        val bundle = Bundle().also {
            it.putString(PlantsFragment.SEARCH, viewModel.country.value?.code ?: "")
            it.putSerializable(PlantsFragment.SEARCH_TYPE, PlantViewModel.TypeOfSearch.GEOGRAPHY)
        }
        this.view?.let {
            Navigation.findNavController(it).navigate(R.id.plantsFragment, bundle)
        }
    }

    private fun initLocationUtils() {
        context?.let {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(it)
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun areLocationUtilsInitiated(): Boolean {
        return this::fusedLocationProviderClient.isInitialized && this::locationManager.isInitialized
    }

    override fun onMapClick(click: LatLng?) {
        if (canClick) {
            clicked = true
            hideFab()
            Log.d(TAG, "clicked")
            overlay.click(googleMap?.projection?.toScreenLocation(click) ?: Point(0, 0))
            viewModel.getProximalGeography(click ?: LatLng(0.0, 0.0))
            saveLocation(click)
            Handler(Looper.getMainLooper()).postDelayed({
                if (!this::dialog.isInitialized || !dialog.isVisible) showFab()
            }, FAB_DURATION)
        }
    }

    private fun checkPermissions() {
        if (areLocationUtilsInitiated()) {
            activity?.let { fragmentActivity ->
                PermissionHelper.checkPermission(fragmentActivity)
            }
        }
    }

    private fun setLocation() {
        context?.let {
            viewModel.getLastLocation(it.getSharedPreferences(SHARED_PREFERENCES, 0))
        }
    }

    private fun getHome() {
        checkPermissions()
        viewModel.getHome(fusedLocationProviderClient, locationManager)
    }

    private fun saveLocation(latLng: LatLng?) {
        context?.let {
            viewModel.setLocation(
                it.getSharedPreferences(SHARED_PREFERENCES, 0),
                latLng ?: LatLng(0.0, 0.0)
            )
        }
    }

    private fun displayPolygons(color: Int) {
        viewModel.country.value?.getPolygonOptions(color)?.forEach {
            viewModel.country.value?.addPolygon(googleMap?.addPolygon(it))
        }
    }

    private fun removePolygons(polygons: List<Polygon>?) {
        polygons?.forEach { polygon ->
            polygon.remove()
        }
    }

    private fun showDialog(geography: Country?) {
        geography?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                this.dialog = MapDialog(geography.name)
                this.dialog.setTargetFragment(this, 0)
                this.dialog.show(parentFragmentManager, "")
            }, 250L)
        }
    }

    private fun centerMapOnHome() {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(viewModel.homeLiveData.value, ZOOM_LVL)
        googleMap?.animateCamera(cameraUpdate)
    }

    private fun relocate(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LVL)
        googleMap?.animateCamera(cameraUpdate)
        updateLastLocationMarker(latLng)
    }

    private fun updateLastLocationMarker(latLng: LatLng) {
        if (this::lastMarker.isInitialized) lastMarker.remove()
        googleMap?.let {
            lastMarker = it.addMarker(
                MarkerOptions()
                    .title(context?.getString(R.string.last_location) ?: "")
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_last))
            )
        }
    }

    private fun showFab() {
        if (viewModel.locationLiveData.value != null) {
            if (this::motionLayout.isInitialized) motionLayout.transitionToEnd()
        }
    }

    private fun hideFab() {
        if (this::motionLayout.isInitialized) motionLayout.transitionToStart()
    }

}