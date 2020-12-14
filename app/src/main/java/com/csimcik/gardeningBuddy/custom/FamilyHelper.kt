package com.csimcik.gardeningBuddy.custom

import android.util.Log
import com.csimcik.gardeningBuddy.models.plantDetail.Family
import com.csimcik.gardeningBuddy.models.entities.FamilyDB
import kotlin.collections.ArrayList

object FamilyHelper {
    val TAG = "FAMILY_HELPER"
    fun convert(list: List<Family>): List<FamilyDB> {
        val listOfFamilyDB = ArrayList<FamilyDB>()
        for (item in list) {
            val divisionOrder = item.division_order
            val divisionClass = divisionOrder?.division_class
            val division = divisionClass?.division
            val subkingdom = division?.subkingdom
            val kingdom = subkingdom?.kingdom
            listOfFamilyDB.add(
                FamilyDB(
                    item.name ?: "",
                    item.common_name ?: "",
                    item.slug ?: "",
                    divisionOrder?.name ?: "",
                    divisionClass?.name ?: "",
                    division?.name ?: "",
                    subkingdom?.name ?: "",
                    kingdom?.name ?: "",
                    ColorHelper.getGreen()
                )
            )
        }
        Log.d(TAG, "${listOfFamilyDB.size}")
        return listOfFamilyDB
    }
}