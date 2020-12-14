package com.csimcik.gardeningBuddy.repositories

import android.content.SharedPreferences
import com.csimcik.Config
import com.csimcik.Config.DISTRIBUTION_SETTING
import com.csimcik.Config.LATLNG
import com.csimcik.gardeningBuddy.viewModels.PlantDetailViewModel
import com.google.android.gms.maps.model.LatLng

class SharedPreferencesRepository {

    fun getLastLocation(sharedPreferences: SharedPreferences): LatLng {
        return sharedPreferences.getString(LATLNG, "").let { string ->
            if (string.isNullOrEmpty()) {
                LatLng(0.0, 0.0)
            } else {
                string.split(":").let { list ->
                    LatLng(list[0].toDouble(), list[1].toDouble())
                }
            }
        }
    }

    fun setLocation(sharedPreferences: SharedPreferences, latLng: LatLng) {
        sharedPreferences.edit().putString(LATLNG, latLng.latitude.toString() + ":" + latLng.longitude.toString()).apply()
    }

    fun getFamilyScrollPosition(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(Config.FAMILY_SCROLL_POSITION, 0)
    }

    fun setFamilyScrollPosition(sharedPreferences: SharedPreferences, scrollPosition: Int) {
        sharedPreferences.edit().putInt(Config.FAMILY_SCROLL_POSITION, scrollPosition).apply()
    }

    fun getDistributionSettings(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(DISTRIBUTION_SETTING, PlantDetailViewModel.NATIVE)
    }

    fun setDistributionSettings(
        sharedPreferences: SharedPreferences,
        distributionSetting: Int
    ) {
        sharedPreferences.edit().putInt(DISTRIBUTION_SETTING, distributionSetting).apply()
    }
}