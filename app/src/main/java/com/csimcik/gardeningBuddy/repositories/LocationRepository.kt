package com.csimcik.gardeningBuddy.repositories

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class LocationRepository : LiveData<LatLng>() {

    @SuppressLint("MissingPermission")
    fun getLocation(client: FusedLocationProviderClient, locationManager: LocationManager) {
        val task = client.lastLocation
        task.addOnSuccessListener { location ->
            when (location) {
                null -> getOldLocation(locationManager)
                else -> setLiveData(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getOldLocation(locationManager: LocationManager) {
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?.let { setLiveData(it) }
    }

    private fun setLiveData(location: Location) {
        value = LatLng(location.latitude, location.longitude)
    }
}