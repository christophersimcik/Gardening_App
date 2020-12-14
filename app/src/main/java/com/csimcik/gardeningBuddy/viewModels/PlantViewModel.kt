package com.csimcik.gardeningBuddy.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.csimcik.gardeningBuddy.models.PlantStub
import com.csimcik.gardeningBuddy.models.Plants
import com.csimcik.gardeningBuddy.repositories.CountriesDatabaseRepository
import com.csimcik.gardeningBuddy.repositories.DatabaseRepository
import com.csimcik.gardeningBuddy.repositories.TrefleRepository
import kotlinx.coroutines.launch

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "PLANT_VIEW_MODEL"
    }

    var totalEntries = 0
        set(value) {
            field = value
            Log.d(TAG, "total entries = $value")
        }

    private var search = ""
    private var page = 1
    private val trefleRepository = TrefleRepository()
    private var typeOfSearch = TypeOfSearch.DEFAULT
    private val countriesDatabaseRepository = CountriesDatabaseRepository(application)
    private val databaseRepository = DatabaseRepository(application)
    private val mutableLiveDataSearchString = MutableLiveData<String>()
    var searchString: LiveData<String> = mutableLiveDataSearchString
    val list = MutableLiveData<ArrayList<PlantStub>>()

    fun getNextPage() {
        page++
    }

    fun setData(search: String, type: TypeOfSearch) {
        this.search = search
        this.typeOfSearch = type
    }

    fun getSearchString() {
        when (typeOfSearch) {
            TypeOfSearch.DEFAULT -> {
                mutableLiveDataSearchString.value = search
            }
            TypeOfSearch.KEYWORD -> {
                mutableLiveDataSearchString.value = search
            }
            TypeOfSearch.FAMILY -> {
                mutableLiveDataSearchString.value = search
            }
            TypeOfSearch.GEOGRAPHY -> {
                viewModelScope.launch {
                    mutableLiveDataSearchString.value =
                        countriesDatabaseRepository.getNameFromCode(search)
                }
            }
        }
    }

    fun getPlants(): LiveData<Plants> {
        when (typeOfSearch) {
            TypeOfSearch.DEFAULT -> {
                return trefleRepository.getPlants(page, search)
            }
            TypeOfSearch.KEYWORD -> {
                return trefleRepository.getPlants(page, search)
            }
            TypeOfSearch.GEOGRAPHY -> {
                return trefleRepository.getPlantsByCountry(search, page)
            }
            TypeOfSearch.FAMILY -> {
                return trefleRepository.getPlants(page, search)
            }
        }
    }

    enum class TypeOfSearch {
        GEOGRAPHY,
        KEYWORD,
        FAMILY,
        DEFAULT
    }

}