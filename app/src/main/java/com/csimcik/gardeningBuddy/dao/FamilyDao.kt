package com.csimcik.gardeningBuddy.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.csimcik.gardeningBuddy.models.entities.FamilyDB

@Dao
interface FamilyDao {
    @Query("SELECT * FROM FamilyDB")
    fun getAll(): LiveData<List<FamilyDB>>

    @Query("SELECT * FROM FamilyDB WHERE divisionOrder IN (:divisionOrder)")
    fun getByDivisionOrder(divisionOrder: String):  LiveData<List<FamilyDB>>

    @Query("SELECT * FROM FamilyDB WHERE divisionClass IN (:divisionClass)")
    fun getByDivisionClass(divisionClass: String):  LiveData<List<FamilyDB>>

    @Query("SELECT * FROM FamilyDB WHERE division IN (:division)")
    fun getByDivision(division: String):  LiveData<List<FamilyDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(families: List<FamilyDB>)

    @Delete
    suspend fun delete(uid: FamilyDB)

    @Query("DELETE FROM FamilyDB")
    suspend fun nukeTable()
}