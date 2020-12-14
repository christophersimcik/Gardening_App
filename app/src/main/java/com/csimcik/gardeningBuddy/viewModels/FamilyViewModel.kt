package com.csimcik.gardeningBuddy.viewModels

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.csimcik.gardeningBuddy.OnDataRetrievedListener
import com.csimcik.gardeningBuddy.custom.FamilyHelper
import com.csimcik.gardeningBuddy.models.plantDetail.Family
import com.csimcik.gardeningBuddy.repositories.DatabaseRepository
import com.csimcik.gardeningBuddy.repositories.SharedPreferencesRepository
import com.csimcik.gardeningBuddy.repositories.TrefleRepository
import kotlinx.coroutines.launch

class FamilyViewModel(application: Application) : AndroidViewModel(application),
    OnDataRetrievedListener {

    companion object {
        const val TAG = "FAMILY_VIEWMODEL"
    }

    private val trefleRepository = TrefleRepository()
    private val databaseRepository = DatabaseRepository(application)
    private val sharedPreferencesRepository = SharedPreferencesRepository()
    val families = databaseRepository.getFamilies()
    lateinit var index: Map<Char, Int>

    init {
        // register to listen for completion of family database
        trefleRepository.onDataRetrievedListener = this
    }

    fun populateLocalDatabase() {
        trefleRepository.populateFamilyDatabase()
    }

    override fun onDataRetrieved(list: List<Any>) {
        val listOfFamilyDB = FamilyHelper.convert(list as List<Family>)
        viewModelScope.launch {
            databaseRepository.insertFamilies(listOfFamilyDB)
        }
    }

    fun makeIndex() {
        val list = families?.value ?: ArrayList()
        val map = HashMap<Char, Int>()
        for (index in list.indices) {
            val char = list[index].name.first()
            if (!map.containsKey(char)) map.put(char, index)
        }
        index = map
    }

    fun getScrollPosition(sharedPreferences: SharedPreferences): Int{
        return sharedPreferencesRepository.getFamilyScrollPosition(sharedPreferences)
    }

    fun setScrollPosition(sharedPreferences: SharedPreferences, scrollPosition: Int){
        sharedPreferencesRepository.setFamilyScrollPosition(sharedPreferences, scrollPosition )
    }
}