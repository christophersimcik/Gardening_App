package com.csimcik.gardeningBuddy.models.plantDetail

import com.csimcik.Config
import com.csimcik.gardeningBuddy.models.*

data class MainSpecies(
    val id: Int?,
    val slug: String?,
    val common_name: String?,
    val scientific_name: String?,
    val family_common_name: String?,
    val image_url: String?,
    val synonyms: List<Synonyms>?,
    val genus: String?,
    val family: String?,
    val edible_part: List<String>?,
    val edible: Boolean?,
    val duration: List<String>?,
    val images: Images?,
    val flower: Flower?,
    val foliage: Foliage?,
    val fruit_or_seed: FruitOrSeed?,
    val specifications: Specifications?,
    val growth: Growth?,
    val distributions: Distributions?
) {

    fun listEdibleParts(): String{
        val stringBuilder = StringBuilder()
        if(edible_part != null){
            for(string in edible_part){
                stringBuilder.append(string)
                if(string != edible_part.last()) stringBuilder.append(", ")
            }
        }
        return if (stringBuilder.isNotEmpty()) stringBuilder.toString() else Config.NO_DATA
    }

    fun listSynonyms(): String {
        val stringBuilder = StringBuilder()
        if (synonyms != null) {
            for (synonym in synonyms) {
                stringBuilder.append(synonym.name)
                if (synonym != synonyms.last()) {
                    stringBuilder.append(",\n\n")
                }
            }
        }
        return if (stringBuilder.isNotEmpty()) stringBuilder.toString() else Config.NO_DATA
    }
}
