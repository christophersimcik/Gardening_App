package com.csimcik.gardeningBuddy.models.responses

import com.csimcik.gardeningBuddy.models.MetaDetail
import com.csimcik.gardeningBuddy.models.plantDetail.PlantDetail

import com.google.gson.annotations.SerializedName

class PlantDetailResponse(
    @SerializedName(value = "data")
    val plant: PlantDetail?,
    @SerializedName(value = "meta")
    val metaData: MetaDetail?
)