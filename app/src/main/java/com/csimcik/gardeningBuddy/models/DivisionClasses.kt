package com.csimcik.gardeningBuddy.models

import com.google.gson.annotations.SerializedName

class DivisionClasses(
    @SerializedName(value = "data")
    val divisionClasses: List<DivisionClass>,
    @SerializedName(value = "meta")
    val metaData: Meta
)
