package com.csimcik.gardeningBuddy.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.csimcik.gardeningBuddy.models.DivisionClass
import com.csimcik.gardeningBuddy.models.DivisionClasses
import com.csimcik.gardeningBuddy.repositories.TrefleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DivisionClassViewModel : ViewModel() {
    companion object {
        const val TAG = "DIVISION_CLASS_VIEW_MODEL"
    }

    val divisionClasses = MutableLiveData<List<DivisionClass>>()
    var division = ""
    var page = 1
    private val repository = TrefleRepository()
    fun getData() {
        repository.getDivisionClasses(page).enqueue(object : Callback<DivisionClasses> {
            override fun onFailure(call: Call<DivisionClasses>, t: Throwable) {
                Log.d(TAG, "ERROR: RETRIEVING DIVISIONS")
            }

            override fun onResponse(
                call: Call<DivisionClasses>,
                response: Response<DivisionClasses>
            ) {
                divisionClasses.postValue(response.body()?.divisionClasses)
            }
        })
    }
}