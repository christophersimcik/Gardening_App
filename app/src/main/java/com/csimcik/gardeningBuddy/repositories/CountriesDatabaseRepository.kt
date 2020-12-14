package com.csimcik.gardeningBuddy.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.csimcik.gardeningBuddy.database.CountriesDatabase
import com.csimcik.gardeningBuddy.models.entities.Country

class CountriesDatabaseRepository(context: Context) {
    private val database = CountriesDatabase.getInstance(context)
    private val dao = database?.countriesDao()

    fun getAll(): LiveData<List<Country>>? {
        return dao?.getAll()
    }

    suspend fun getNameFromCode(code: String): String{
      return dao?.getNameOfCountryFromCode(code) ?: ""
    }

    suspend fun nukeTable(){
        dao?.nukeTable()
    }


}