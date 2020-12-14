package com.csimcik.gardeningBuddy.models.plantDetail

data class Specifications(
    val ligneous_type : String,
    val growth_form : String,
    val growth_habit : String,
    val growth_rate : String,
    val average_height : AverageHeight,
    val maximum_height : MaximumHeight,
    val nitrogen_fixation : String,
    val shape_and_orientation : String,
    val toxicity : String
)