package com.csimcik.gardeningBuddy.models

data class Species(
    val commonNames : List<String>,
    val observations : String,
    val edible : Boolean,
    val vegetable : Boolean,
    val edibleParts : List<String>,
    val duration : List<String>,
    val imageUrl : String,
    val taxonomicRank : String,
    val commonFamilyName : String,
    val scientificName : String,
    val commonName : String,
    val species : String,
    val plant : String,
    val genus : String,
    val family : String,
    val divisionOrder : String,
    val divisionClass : String,
    val division : String,
    val subKingdom : String
)