package com.csimcik.gardeningBuddy.viewModels

import retrofit2.Call
import android.util.Log
import retrofit2.Response
import retrofit2.Callback
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import com.csimcik.gardeningBuddy.models.*
import com.csimcik.gardeningBuddy.ITEMS_PER_PAGE
import com.csimcik.gardeningBuddy.models.Division
import com.csimcik.gardeningBuddy.repositories.TrefleRepository

class DivisionViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "DIVISION_VIEWMODEL"
    }

    val divisions = MutableLiveData<List<Division>>()
    private var listOfFamilies: MutableList<Division> = ArrayList()
    private val repository = TrefleRepository()
    private var page = 1
    private var totalPages = 1
    var entriesReturnedCount = 0

    fun getNextPage() {
        if (page < totalPages) {
            page++
            getData()
        }
    }

    fun getData() {
        repository.getDivisions(page).enqueue(object : Callback<Divisions> {
            override fun onFailure(call: Call<Divisions>, t: Throwable) {
                Log.d(TAG, "ERROR: RETRIEVING DIVISIONS")
            }

            override fun onResponse(
                call: Call<Divisions>,
                response: Response<Divisions>
            ) {
                response.body()?.let { it.divisions.let { listOfFamilies.addAll(it) } }
                setTotal(response)
                divisions.postValue(listOfFamilies)
            }
        })
    }

    fun setTotal(response: Response<Divisions>) {
        entriesReturnedCount = (response.body()?.metaData?.total) ?: ITEMS_PER_PAGE
        totalPages = entriesReturnedCount / ITEMS_PER_PAGE
        Log.i(TAG, "total pages = $totalPages entries = $entriesReturnedCount")
    }
}