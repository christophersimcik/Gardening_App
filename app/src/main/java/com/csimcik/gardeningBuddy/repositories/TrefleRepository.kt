package com.csimcik.gardeningBuddy.repositories


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.csimcik.gardeningBuddy.BuildConfig
import com.csimcik.gardeningBuddy.OnDataRetrievedListener
import com.csimcik.gardeningBuddy.models.*
import com.csimcik.gardeningBuddy.models.plantDetail.Family
import com.csimcik.gardeningBuddy.models.plantDetail.PlantDetail
import com.csimcik.gardeningBuddy.models.responses.PlantDetailResponse
import com.csimcik.gardeningBuddy.services.TrefleApiService
import com.csimcik.gardeningBuddy.viewModels.FamilyViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrefleRepository {
    val trefleApiService = TrefleApiService.create()
    val tokenAsString: String
    private val list = ArrayList<Any>()
    lateinit var onDataRetrievedListener: OnDataRetrievedListener
    var numberOfEntries = 0
    var page = 1

    companion object {
        const val TAG = "REPOSITORY"
    }

    init {
        tokenAsString = BuildConfig.TREFLE_API_KEY
    }

    external fun getToken(): String

    fun getDivisions(page: Int): Call<Divisions> {
        return trefleApiService.getDivisions(tokenAsString, page)
    }

    fun getDivisionClasses(page: Int): Call<DivisionClasses> {
        return trefleApiService.getDivisionClasses(tokenAsString, page)
    }

    fun getDivisionOrders(page: Int): Call<DivisionOrders> {
        return trefleApiService.getDivisionOrders(tokenAsString, page)
    }

    fun getFamilies(page: Int): Call<Families> {
        return trefleApiService.getFamilies(tokenAsString, page)
    }

    fun getPlantDetail(url: String): LiveData<PlantDetail> {
        val data = MutableLiveData<PlantDetail>()
        trefleApiService.getPlantDetail(url, tokenAsString)
            .enqueue(object : Callback<PlantDetailResponse> {
                override fun onResponse(
                    call: Call<PlantDetailResponse>,
                    response: Response<PlantDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            TAG,
                            "successful, msg: ${response.message()} code: ${response.code()}"
                        )
                        data.value = response.body()?.plant
                    } else {
                        Log.d(TAG, "failed, msg: ${response.message()} code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PlantDetailResponse>, t: Throwable) {
                    Log.d(TAG, "ON FAILURE: ${t.message}")
                    getPlantDetail(url)
                }

            })
        return data
    }

    fun getPlantsByCountry(code: String, page: Int): LiveData<Plants> {
        Log.d(TAG, "code = $code and page = $page")
        val data = MutableLiveData<Plants>()
        trefleApiService.getPlantsByCountry(code, page, tokenAsString)
            .enqueue(object : Callback<Plants> {
                override fun onResponse(call: Call<Plants>, response: Response<Plants>) {
                    if (response.isSuccessful) {
                        Log.d(
                            TAG,
                            "successful, msg: ${response.message()} code: ${response.code()}"
                        )
                        data.value = response.body()?.let { response.body() }
                        Log.d(TAG, "plant entries = ${response.body()?.metaData?.total}")
                    } else {
                        Log.d(TAG, "failed, msg: ${response.message()} code: ${response.code()}")
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<Plants>, t: Throwable) {
                    Log.d(TAG, "ON FAILURE: ${t.message}")
                    getPlants(page, code)
                }
            })
        return data
    }

    fun getPlants(page: Int, search: String): LiveData<Plants> {
        Log.d(TAG, "search = $search")
        val data = MutableLiveData<Plants>()
        trefleApiService.getPlants(tokenAsString, page, search).enqueue(object : Callback<Plants> {
            override fun onResponse(call: Call<Plants>, response: Response<Plants>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "successful, msg: ${response.message()} code: ${response.code()}")
                    data.value = response.body()?.let { response.body() }
                    Log.d(TAG, "plant entries = ${response.body()?.metaData?.total}")
                } else {
                    Log.d(TAG, "failed, msg: ${response.message()} code: ${response.code()}")
                    data.value = null
                }
            }

            override fun onFailure(call: Call<Plants>, t: Throwable) {
                Log.d(TAG, "ON FAILURE: ${t.message}")
                getPlants(page, search)
            }
        })
        return data
    }

    fun populateFamilyDatabase(): List<Family> {
        reset()
        getPage()
        return list as List<Family>
    }

    private fun getPage() {
        Log.d(FamilyViewModel.TAG, "calling")
        getFamilies(page).enqueue(object : Callback<Families> {
            override fun onFailure(call: Call<Families>, t: Throwable) {
                Log.d(FamilyViewModel.TAG, "ERROR: RETRIEVING DIVISIONS")
                getPage()
            }

            override fun onResponse(call: Call<Families>, response: Response<Families>) {
                response.body()?.let {
                    list.add(it.families)
                    onDataRetrievedListener.onDataRetrieved(it.families)
                    numberOfEntries = it.metaData.total ?: 0
                    if (list.size <= numberOfEntries) {
                        page++
                        getPage()
                    }
                }
            }
        })
    }

    private fun reset() {
        list.clear()
        page = 1
        numberOfEntries = 0
    }


}