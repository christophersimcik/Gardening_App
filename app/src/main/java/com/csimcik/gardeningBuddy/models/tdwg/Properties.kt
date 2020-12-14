package com.csimcik.gardeningBuddy.models.tdwg

import com.google.gson.annotations.SerializedName

class Properties(
    @SerializedName(value = "Level_4_Na")
    val name: String,
    @SerializedName(value = "Level3_cod")
    val code: String,
    @SerializedName(value = "ISO_Code")
    val iso: String
)