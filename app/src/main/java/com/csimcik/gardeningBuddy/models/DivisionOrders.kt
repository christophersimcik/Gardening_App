package com.csimcik.gardeningBuddy.models

import com.google.gson.annotations.SerializedName

class DivisionOrders(
    @SerializedName(value = "data")
    val divisionOrders: List<DivisionOrder>,
    @SerializedName(value = "meta")
    val metaData: Meta
)