package com.csimcik.gardeningBuddy.viewModels

import android.app.Application
import android.content.SharedPreferences
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.csimcik.gardeningBuddy.models.entities.Country
import com.csimcik.gardeningBuddy.repositories.CountriesDatabaseRepository
import com.csimcik.gardeningBuddy.repositories.LocationRepository
import com.csimcik.gardeningBuddy.repositories.SharedPreferencesRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository()
    private val sharedPreferencesRepository = SharedPreferencesRepository()
    private val countriesRepository = CountriesDatabaseRepository(application)
    private val mutableLocationLiveData = MutableLiveData<LatLng>()
    val homeLiveData: LiveData<LatLng> = locationRepository
    val locationLiveData: LiveData<LatLng> = mutableLocationLiveData
    val geography = MutableLiveData<Country>()
    val country: LiveData<Country> = geography
    var countries: List<Country> = ArrayList()

    fun getCountries(): LiveData<List<Country>>{
        return countriesRepository.getAll() ?: MutableLiveData()
    }

    fun getHome(client: FusedLocationProviderClient, manager: LocationManager) {
            locationRepository.getLocation(client, manager)
    }

    fun getLastLocation(sharedPreferences: SharedPreferences){
       mutableLocationLiveData.value = sharedPreferencesRepository.getLastLocation(sharedPreferences)
    }

    fun setLocation(sharedPreferences: SharedPreferences, latlng: LatLng){
        sharedPreferencesRepository.setLocation(sharedPreferences, latlng)
    }

    fun getProximalGeography(latLng: LatLng){
        viewModelScope.launch {
            val list = ArrayList<Country>().also {
                countries.forEach { geography ->
                    if (geography.getLatLngBounds().contains(latLng)) {
                        it.add(geography)
                    }
                }
            }
            if(list.isNotEmpty()) geography.value = resolveNearestCountry(list, latLng)
        }
    }

    private fun resolveNearestCountry(list: List<Country>, click: LatLng): Country {
        var nearestCountry = list.first()
        if (list.size == 1) return nearestCountry
        var minDistance =
            SphericalUtil.computeDistanceBetween(click, nearestCountry.getLatLngBounds().center)
        for (i in 1..list.lastIndex) {
            val next = list[i]
            val distance =
                SphericalUtil.computeDistanceBetween(click, next.getLatLngBounds().center)
            if (distance < minDistance) {
                nearestCountry = next
                minDistance = distance
            }
        }
        return nearestCountry
    }

}