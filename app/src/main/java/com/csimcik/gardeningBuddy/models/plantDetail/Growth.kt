package com.csimcik.gardeningBuddy.models.plantDetail

data class Growth(
    val days_to_harvest : Int?,
    val decsription : String?,
    val sowing : String?,
    val ph_maximum : Float?,
    val ph_minimum : Float?,
    val light : Int?,
    val atmospheric_humidity : Int?,
    val growth_months : List<String>?,
    val bloom_months : List<String>?,
    val fruit_months : List<String>?,
    val minimum_precipitation : MaximumPrecipitation?,
    val maximum_precipitation : MinimumPrecipitation?,
    val minimum_temperature : MinimumTemperature?,
    val maximum_tempwerature : MaximumTemperature?,
    val soil_nutriments : Int?,
    val soil_salinity : Int?,
    val soil_texture : Int?,
    val soil_humidity : Int?
)