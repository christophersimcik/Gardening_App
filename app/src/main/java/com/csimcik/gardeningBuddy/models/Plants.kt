package com.csimcik.gardeningBuddy.models

import com.google.gson.annotations.SerializedName

class Plants (
    @SerializedName(value = "data")
    val plants: List<PlantStub>,
    @SerializedName(value = "meta")
    val metaData: Meta
)