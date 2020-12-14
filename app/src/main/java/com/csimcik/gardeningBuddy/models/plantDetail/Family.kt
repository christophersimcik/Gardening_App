package com.csimcik.gardeningBuddy.models.plantDetail

import com.csimcik.gardeningBuddy.models.DivisionOrder

data class Family(
    val name : String?,
    val common_name : String?,
    val slug : String?,
    val division_order : DivisionOrder?,
)