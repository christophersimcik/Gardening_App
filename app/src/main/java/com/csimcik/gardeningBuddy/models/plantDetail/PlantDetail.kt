package com.csimcik.gardeningBuddy.models.plantDetail

import com.csimcik.gardeningBuddy.models.plantDetail.Family
import com.csimcik.gardeningBuddy.models.plantDetail.Genus
import com.csimcik.gardeningBuddy.models.plantDetail.Link
import com.csimcik.gardeningBuddy.models.plantDetail.MainSpecies
import com.google.gson.annotations.SerializedName

data class PlantDetail(
    val id : Int?,
    val slug : String?,
    val common_name : String?,
    val scientific_name : String?,
    val family_common_name : String?,
    val image_url : String?,
    val synonyms : List<String>?,
    val observation : String?,
    val genus : Genus?,
    val family : Family?,
    val links : Link?,
    @SerializedName(value = "main_species")
    val mainSpecies: MainSpecies?,
)