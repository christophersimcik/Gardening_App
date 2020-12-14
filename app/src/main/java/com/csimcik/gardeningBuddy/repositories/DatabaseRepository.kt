package com.csimcik.gardeningBuddy.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.csimcik.gardeningBuddy.database.TrefleDatabase
import com.csimcik.gardeningBuddy.models.entities.FamilyDB

class DatabaseRepository(context: Context) {
    private val database = TrefleDatabase.getInstance(context)
    private val dao = database?.familyDao()

    fun getFamilies(): LiveData<List<FamilyDB>>? {
        return dao?.getAll()
    }

    fun getFamilesByDivisionClass(divisionClass: String): LiveData<List<FamilyDB>>? {
        return dao?.getByDivisionClass(divisionClass)
    }

    fun getFamilesByDivisionOrder(divisionOrder: String): LiveData<List<FamilyDB>>? {
        return dao?.getByDivisionClass(divisionOrder)
    }

    fun getFamilesByDivision(division: String): LiveData<List<FamilyDB>>? {
        return dao?.getByDivisionClass(division)
    }

    suspend fun insertFamilies(list: List<FamilyDB>) {
        dao?.insertAll(list)
    }

    suspend fun nukeTable(){
        dao?.nukeTable()
    }


}