package com.csimcik.gardeningBuddy.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.csimcik.gardeningBuddy.models.entities.Country

@Dao
interface CountriesDao {
    @Query("SELECT * FROM Country")
    fun getAll(): LiveData<List<Country>>

    @Query("SELECT name FROM Country WHERE code IN (:code)")
    suspend fun getNameOfCountryFromCode(code: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(families: List<Country>)

    @Delete
    suspend fun delete(uid: Country)

    @Query("DELETE FROM Country")
    suspend fun nukeTable()
}