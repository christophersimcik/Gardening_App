package com.csimcik.gardeningBuddy.services

import com.csimcik.gardeningBuddy.models.*
import com.csimcik.gardeningBuddy.models.responses.PlantDetailResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrefleApiService {

    companion object {
        const val TAG = "REPOSITORY"
        fun create(): TrefleApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://trefle.io/api/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(TrefleApiService::class.java)
        }
    }

    @GET("divisions?")
    fun getDivisions(
        @Query("token") token: String,
        @Query("page") page: Int
    ): Call<Divisions>

    @GET("division_classes?")
    fun getDivisionClasses(
        @Query("token") token: String,
        @Query("page") page: Int
    ): Call<DivisionClasses>

    @GET("division_orders?")
    fun getDivisionOrders(
        @Query("token") token: String,
        @Query("page") page: Int
    ): Call<DivisionOrders>

    @GET("families?")
    fun getFamilies(
        @Query("token") token: String,
        @Query("page") page: Int
    ): Call<Families>

    @GET("plants/search")
    fun getPlants(
        @Query("token") token: String,
        @Query("page") page: Int,
        @Query("q") query: String
    ): Call<Plants>

    @GET("plants/{id}?")
    fun getPlantDetail(
        @Path("id") id: String,
        @Query("token") token: String
    ): Call<PlantDetailResponse>

    @GET("distributions/{code}/plants?")
    fun getPlantsByCountry(
        @Path("code") code: String,
        @Query("page") page: Int,
        @Query("token") token: String
    ): Call<Plants>
}