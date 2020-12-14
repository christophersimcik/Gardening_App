package com.csimcik.gardeningBuddy.models

import com.google.gson.annotations.SerializedName

class Divisions(
    @SerializedName(value = "data")
    val divisions: List<Division>,
    @SerializedName(value = "meta")
    val metaData: Meta
)