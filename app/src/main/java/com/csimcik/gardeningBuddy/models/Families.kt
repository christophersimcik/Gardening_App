package com.csimcik.gardeningBuddy.models

import com.csimcik.gardeningBuddy.models.plantDetail.Family
import com.google.gson.annotations.SerializedName

class Families(
    @SerializedName(value = "data")
    val families: List<Family>,
    @SerializedName(value = "meta")
    val metaData: Meta
)