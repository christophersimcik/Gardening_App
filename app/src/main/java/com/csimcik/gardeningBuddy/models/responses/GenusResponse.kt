package com.csimcik.gardeningBuddy.models.responses

import com.csimcik.gardeningBuddy.models.plantDetail.Genus
import com.google.gson.annotations.SerializedName

class GenusResponse(
    @SerializedName(value = "data")
    val genera: List<Genus>
) {
    fun getList(): List<Genus> {
        return genera
    }
}