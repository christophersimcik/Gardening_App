package com.csimcik.gardeningBuddy.viewModels

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.csimcik.gardeningBuddy.models.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.csimcik.gardeningBuddy.BRUTAL
import com.csimcik.gardeningBuddy.models.plantDetail.PlantDetail
import com.csimcik.gardeningBuddy.models.plantDetail.Zone
import com.csimcik.gardeningBuddy.repositories.SharedPreferencesRepository
import com.csimcik.gardeningBuddy.repositories.TrefleRepository
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.*

class PlantDetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "PLANT_DETAIL_VIEWMODEL"
        const val NATIVE = 0
        const val INTRODUCED = 1
        const val LVL_3_CODE = "Level3_cod"
        const val NAME = "Level_4_Na"
        const val LO_POLY_NAME = "name"
    }

    var plant: String? = ""
    var plantName: String? = ""
    var plantDetail: PlantDetail? = null
    var nativeGeoJsonFeatures: MutableList<GeoJsonFeature> = ArrayList()
    var introducedGeoJsonFeatures: MutableList<GeoJsonFeature> = ArrayList()
    var distributionSettings = NATIVE
    val hasDistributionData = MutableLiveData<Boolean>().apply { value = true }
    val distributionLiveData = hasDistributionData
    private val trefleRepository = TrefleRepository()
    private val sharedPreferencesRepository = SharedPreferencesRepository()
    var map = BRUTAL.createMap()
    var listener: FeaturesCompiledCallback? = null

    fun getPlant(): LiveData<PlantDetail> {
        return trefleRepository.getPlantDetail(plant ?: "")
    }

    fun setDistributionSetting(sharedPreferences: SharedPreferences, setting: Int) {
        sharedPreferencesRepository.setDistributionSettings(sharedPreferences, setting)
        distributionSettings = setting
    }

    fun getDistributionSetting(sharedPreferences: SharedPreferences, setting: Int) {
        distributionSettings =
            sharedPreferencesRepository.getDistributionSettings(sharedPreferences)
    }

    fun setGeoJsonFeatures(layer: GeoJsonLayer) {
        clearGeoJsonFeatureLists()
        val allFeatures = layer.features
        val nativeZones = plantDetail?.let { it.mainSpecies?.distributions?.native } ?: ArrayList()
        val introducedZones =
            plantDetail?.let { it.mainSpecies?.distributions?.introduced } ?: ArrayList()
        CoroutineScope(Dispatchers.Default).launch {
            iterateFeatures(allFeatures, nativeZones, introducedZones)
            withContext(Dispatchers.Main) {
                listener?.onFeaturesCompiled()
            }
        }
    }

     fun checkDistributionData(option: Int) {
        when (option) {
            NATIVE -> hasDistributionData.value = nativeGeoJsonFeatures.isNotEmpty()
            INTRODUCED -> hasDistributionData.value = introducedGeoJsonFeatures.isNotEmpty()
        }
    }

    private fun iterateFeatures(
        features: Iterable<GeoJsonFeature>,
        nativeZones: List<Zone>,
        introducedZones: List<Zone>
    ) {
        for (feature in features) {
            searchNativeZones(nativeZones, feature)
            searchIntroducedZones(introducedZones, feature)
        }
    }

    fun tempMakeList(features: Iterable<GeoJsonFeature>) {
        val sb = StringBuilder()
        for (feature in features) {
            val code = feature.getProperty(LVL_3_CODE)
            val name = feature.getProperty(NAME)
            sb.append("$code:$name")
            if (feature != features.last()) sb.append(",")
        }
        Log.d(TAG, "sz = $sb")

    }

    private fun searchNativeZones(zones: List<Zone>, feature: GeoJsonFeature) {
        val name = feature.getProperty(LO_POLY_NAME)
        for (zone in zones) {
            if (zone.name == name) {
                if (!nativeGeoJsonFeatures.contains(feature)) {
                    nativeGeoJsonFeatures.add(feature)
                }
            }
        }
    }

    private fun searchIntroducedZones(
        zones: List<Zone>,
        feature: GeoJsonFeature
    ) {
        val name = feature.getProperty(LO_POLY_NAME)
        for (zone in zones) {
            if (zone.name == name) {
                if (!introducedGeoJsonFeatures.contains(feature)) {
                    introducedGeoJsonFeatures.add(feature)
                }
            }
        }
    }

    private fun clearGeoJsonFeatureLists() {
        nativeGeoJsonFeatures.clear()
        introducedGeoJsonFeatures.clear()
    }

    interface FeaturesCompiledCallback {
        fun onFeaturesCompiled()
    }

}
