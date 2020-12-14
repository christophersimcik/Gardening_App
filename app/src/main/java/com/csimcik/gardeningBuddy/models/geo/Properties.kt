package com.csimcik.gardeningBuddy.models.geo

import com.google.gson.annotations.SerializedName

class Properties(
    val name: String,
    @SerializedName(value = "sov_a3")
    val code: String,
    @SerializedName(value = "iso_a2")
    val iso: String
)